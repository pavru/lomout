package net.pototskiy.apps.lomout.printer

import net.pototskiy.apps.lomout.LogCatcher
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.database.EntityStatus
import net.pototskiy.apps.lomout.api.entity.DoubleType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.entity.StringType
import net.pototskiy.apps.lomout.api.entity.get
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import net.pototskiy.apps.lomout.database.initDatabase
import net.pototskiy.apps.lomout.loader.DataLoader
import net.pototskiy.apps.lomout.mediator.DataMediator
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import java.io.File

@Suppress("ComplexMethod", "MagicNumber")
internal class PrinterBasicTest {
    private val typeManager = EntityTypeManager()
    private val helper = ConfigBuildHelper(typeManager)
    private val testDataDir = System.getenv("TEST_DATA_DIR")
    private val fileName = "$testDataDir/mediator-test-data.xls"
    private val outputName = "../tmp/printer-basic-test.xls"

    @Test
    internal fun printerBasicTest() {
        File("../tmp/$outputName").parentFile.mkdirs()
        val config = createConfiguration()
        System.setProperty("mediation.line.cache.size", "4")
        System.setProperty("printer.line.cache.size", "4")
        initDatabase(config.database, typeManager)
        transaction { DbEntityTable.deleteAll() }
        DataLoader.load(config)
        DataMediator.mediate(config)
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataPrinter.print(config)
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(log).doesNotContain("[ERROR]")
        val entities = DbEntity.getEntities(typeManager["import-data"], true)
        WorkbookFactory.create(File(outputName).toURI().toURL()).use { workbook ->
            val sheet = workbook["test"]
            assertThat(sheet).isNotNull
            val headRow = sheet[0]
            assertThat(headRow).isNotNull
            assertThat(headRow!![0]?.stringValue).isEqualTo("sku")
            assertThat(headRow[1]?.stringValue).isEqualTo("desc")
            assertThat(headRow[2]?.stringValue).isEqualTo("corrected_amount")
            for (i in 1..7 step 2) {
                val type = entities.first().entityType
                val sku = sheet[i + 1]!![0]?.longValue
                val entity = entities.findLast { it.data[type["sku"]]?.value == sku }!!
                assertThat(sheet[i]!![0]?.doubleValue)
                    .isNotNull().isEqualTo(entity.data[type["amount"]]!!.value)
                assertThat(sheet[i + 1]!![1]?.stringValue)
                    .isNotNull().isEqualTo(entity.data[type["desc"]]!!.value)
                assertThat(sheet[i + 1]!![2]?.doubleValue)
                    .isNotNull().isEqualTo(entity.data[type["corrected_amount"]]!!.value)
            }
        }
    }

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
                    attribute<LongType>("sku") {
                        key()
                        writer { value, cell -> value?.let { cell.setCellValue(it.value) } }
                    }
                    attribute<StringType>("desc")
                    attribute<DoubleType>("amount") {
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
                    main("entity1") {
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
                    main("entity1") {
                        field("sku") { column(0) }
                        field("desc") { column(1) }
                        field("amount") { column(2) }
                    }
                }
            }
        }
        mediator {
            crossProductionLine {
                input {
                    entity("entity1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] neq EntityStatus.REMOVED
                            }
                        }
                        extAttribute<DoubleType>("corrected_amount", "amount") {
                            reader { _, cell ->
                                DoubleType(cell.doubleValue * 11.0)
                            }
                        }
                    }
                    entity("entity2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] neq EntityStatus.REMOVED
                            }
                        }
                        extAttribute<DoubleType>("corrected_amount", "amount") {
                            reader { _, cell ->
                                DoubleType(cell.doubleValue * 13.0)
                            }
                        }
                    }
                }
                output("import-data") {
                    inheritFrom("entity1")
                    attribute<DoubleType>("corrected_amount") {
                        writer { value, cell -> value?.let { cell.setCellValue(it.value) } }
                    }
                }
                pipeline {
                    classifier { entities: PipelineDataCollection ->
                        if (entities[0]["sku"]?.value == entities[1]["sku"]?.value) {
                            Pipeline.CLASS.MATCHED
                        } else {
                            Pipeline.CLASS.UNMATCHED
                        }
                    }
                    pipeline(Pipeline.CLASS.MATCHED) {
                        assembler { target, entities ->
                            mapOf(
                                target["sku"] to entities[0]["sku"],
                                target["desc"] to entities[1]["desc"],
                                target["amount"] to entities[1]["amount"],
                                target["corrected_amount"] to entities[0]["corrected_amount"]
                            )
                        }
                    }
                    pipeline(Pipeline.CLASS.UNMATCHED) {
                        classifier { entities ->
                            if (entities[0].entity.entityType.name == "entity2") {
                                Pipeline.CLASS.MATCHED
                            } else {
                                Pipeline.CLASS.UNMATCHED
                            }
                        }
                        assembler { target, entities ->
                            mapOf(
                                target["sku"] to entities[0]["sku"],
                                target["desc"] to entities[0]["desc"],
                                target["amount"] to entities[0]["amount"],
                                target["corrected_amount"] to entities[0]["corrected_amount"]
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
                        filter { with(DbEntityTable) { it[currentStatus] neq EntityStatus.REMOVED } }
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
                    classifier { Pipeline.CLASS.MATCHED }
                    assembler { _, entities ->
                        entities.first().data
                    }
                }
            }
        }
    }.build()
}
