package net.pototskiy.apps.lomout.printer

import net.pototskiy.apps.lomout.LogCatcher
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.EntityStatus
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.get
import net.pototskiy.apps.lomout.api.entity.type.DOUBLE
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import net.pototskiy.apps.lomout.loader.DataLoader
import net.pototskiy.apps.lomout.mediator.DataMediator
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import java.io.File

@Suppress("ComplexMethod", "MagicNumber")
internal class PrinterBasicTest {
    private val typeManager = EntityTypeManagerImpl()
    private val helper = ConfigBuildHelper(typeManager)
    private val testDataDir = System.getenv("TEST_DATA_DIR")
    private val fileName = "$testDataDir/mediator-test-data.xls"
    private val outputName = "../tmp/printer-basic-test.xls"

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun printerBasicTest() {
        File("../tmp/$outputName").parentFile.mkdirs()
        val config = createConfiguration()

        PluginContext.config = config
        PluginContext.entityTypeManager = config.entityTypeManager
        PluginContext.scriptFile = File("no-file.conf.kts")

        System.setProperty("mediation.line.cache.size", "4")
        System.setProperty("printer.line.cache.size", "4")
        val repository = EntityRepository(config.database, typeManager, Level.ERROR)
        PluginContext.repository = repository
        repository.getIDs(typeManager["entity1"]).forEach { repository.delete(it) }
        repository.getIDs(typeManager["entity2"]).forEach { repository.delete(it) }
        repository.getIDs(typeManager["import-data"]).forEach { repository.delete(it) }
        DataLoader.load(repository, config)
        DataMediator.mediate(repository, config)
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataPrinter.print(repository, config)
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(log).doesNotContain("[ERROR]")
        val entities = repository.get(typeManager["import-data"])
        WorkbookFactory.create(File(outputName).toURI().toURL()).use { workbook ->
            val sheet = workbook["test"]
            assertThat(sheet).isNotNull
            val headRow = sheet[0]
            assertThat(headRow).isNotNull
            assertThat(headRow!![0]?.stringValue).isEqualTo("sku")
            assertThat(headRow[1]?.stringValue).isEqualTo("desc")
            assertThat(headRow[2]?.stringValue).isEqualTo("corrected_amount")
            for (i in 1..7 step 2) {
                val type = entities.first().type
                val sku = sheet[i + 1]!![0]?.longValue
                val entity = entities.findLast { it.data[type["sku"]]?.value == sku }!!
                @Suppress("UsePropertyAccessSyntax")
                assertThat(sheet[i]!![0]?.doubleValue)
                    .isNotNull().isEqualTo(entity.data[type["amount"]]!!.value)
                @Suppress("UsePropertyAccessSyntax")
                assertThat(sheet[i + 1]!![1]?.stringValue)
                    .isNotNull().isEqualTo(entity.data[type["desc"]]!!.value)
                @Suppress("UsePropertyAccessSyntax")
                assertThat(sheet[i + 1]!![2]?.doubleValue)
                    .isNotNull().isEqualTo(entity.data[type["corrected_amount"]]!!.value)
            }
        }
        repository.close()
    }

    @Suppress("LongMethod")
    private fun createConfiguration() = Config.Builder(helper).apply {
        database {
            name("test_lomout")
            server {
                host("localhost")
                port(3306)
                user("root")
                password(if (System.getenv("TRAVIS_BUILD_DIR") == null) "root" else "")
            }
        }
        loader {
            files {
                file("test-data") { path(fileName) }
            }
            entities {
                entity("entity1", false) {
                    attribute<LONG>("sku") {
                        key()
                        writer { value, cell -> value?.let { cell.setCellValue(it.value) } }
                    }
                    attribute<STRING>("desc")
                    attribute<DOUBLE>("amount") {
                        writer { value, cell -> value?.let { cell.setCellValue(it.value) } }
                    }
                }
                entity("entity2", false) {
                    inheritFrom("entity1")
                }
            }
            loadEntity("entity1") {
                fromSources { source { file("test-data"); sheet("entity1"); stopOnEmptyRow() } }
                rowsToSkip(1)
                keepAbsentForDays(1)
                sourceFields {
                    main("entity") {
                        field("sku") { column(0) }
                        field("desc") { column(1) }
                        field("amount") { column(2) }
                    }
                }
            }
            loadEntity("entity2") {
                fromSources { source { file("test-data"); sheet("entity2"); stopOnEmptyRow() } }
                rowsToSkip(1)
                keepAbsentForDays(1)
                sourceFields {
                    main("entity") {
                        field("sku") { column(0) }
                        field("desc") { column(1) }
                        field("amount") { column(2) }
                    }
                }
            }
        }
        mediator {
            productionLine {
                input {
                    entity("entity1") {
                        statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                        extAttribute<DOUBLE>("corrected_amount") {
                            builder {
                                DOUBLE((it["amount"] as DOUBLE).value * 11.0)
                            }
                        }
                    }
                    entity("entity2") {
                        statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                        extAttribute<DOUBLE>("corrected_amount") {
                            builder {
                                DOUBLE((it["amount"] as DOUBLE).value * 13.0)
                            }
                        }
                    }
                }
                output("import-data") {
                    inheritFrom("entity1")
                    attribute<DOUBLE>("corrected_amount") {
                        writer { value, cell -> value?.let { cell.setCellValue(it.value) } }
                    }
                }
                pipeline {
                    classifier { element ->
                        var entity = element.entities.getOrNull("entity1")
                        if (entity != null) {
                            val partnerType = entityTypeManager["entity2"]
                            val partner = repository.get(
                                partnerType,
                                mapOf(partnerType["sku"] to entity["sku"]!!),
                                EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED
                            )
                            if (partner != null) {
                                element.match(partner)
                            } else {
                                element.mismatch()
                            }
                        } else if (element.entities.getOrNull("entity2") != null) {
                            entity = element.entities["entity2"]
                            val partnerType = entityTypeManager["entity1"]
                            val partner = repository.get(
                                partnerType,
                                mapOf(partnerType["sku"] to entity["sku"]!!),
                                EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED
                            )
                            if (partner != null) {
                                element.skip()
                            } else {
                                element.mismatch()
                            }
                        } else {
                            element.skip()
                        }
                    }
                    pipeline(Pipeline.CLASS.MATCHED) {
                        assembler { target, entities ->
                            mapOf(
                                target["sku"] to entities[0]["sku"]!!,
                                target["desc"] to entities[1]["desc"]!!,
                                target["amount"] to entities[1]["amount"]!!,
                                target["corrected_amount"] to entities[0]["corrected_amount"]!!
                            )
                        }
                    }
                    pipeline(Pipeline.CLASS.UNMATCHED) {
                        classifier {
                            val entities = it.entities
                            if (entities[0].type.name == "entity2") {
                                it.match()
                            } else {
                                it.mismatch()
                            }
                        }
                        assembler { target, entities ->
                            mapOf(
                                target["sku"] to entities[0]["sku"]!!,
                                target["desc"] to entities[0]["desc"]!!,
                                target["amount"] to entities[0]["amount"]!!,
                                target["corrected_amount"] to entities[0]["corrected_amount"]!!
                            )
                        }
                    }
                }
            }
        }
        printer {
            files {
                file("output") { path(outputName) }
            }
            printerLine {
                input {
                    entity("import-data") {
                        statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                    }
                }
                output {
                    file { file("output"); sheet("test") }
                    printHead = true
                    outputFields {
                        main("main-data") {
                            field("sku") { column(0) }
                            field("desc") { column(1) }
                            field("corrected_amount") { column(2) }
                        }
                        extra("extra-data") {
                            field("amount") { column(0) }
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, entities ->
                        entities.first().data
                    }
                }
            }
        }
    }.build()
}
