package net.pototskiy.apps.lomout.mediator

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface.Companion.ACTUAL_ENTITY
import net.pototskiy.apps.lomout.api.entity.EntityStatus
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.get
import net.pototskiy.apps.lomout.api.entity.type.DOUBLE
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.loader.DataLoader
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import java.io.File

@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
@Execution(ExecutionMode.SAME_THREAD)
@Suppress("MagicNumber")
internal class MediatorBasicTest {
    private val typeManager = EntityTypeManagerImpl()
    private val helper = ConfigBuildHelper(typeManager)

    @Suppress("LongMethod")
    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun complexBasicTest() {
        val config = createConfiguration()
        PluginContext.config = config
        PluginContext.entityTypeManager = config.entityTypeManager
        PluginContext.scriptFile = File("no-file.conf.kts")

        System.setProperty("mediation.line.cache.size", "4")
        val repository = EntityRepository(config.database, typeManager, Level.ERROR)
        PluginContext.repository = repository
        repository.getIDs(typeManager["entity1"]).forEach { repository.delete(it) }
        repository.getIDs(typeManager["entity2"]).forEach { repository.delete(it) }
        repository.getIDs(typeManager["import-data"]).forEach { repository.delete(it) }
        repository.getIDs(typeManager["import-data-union"]).forEach { repository.delete(it) }

        DataLoader.load(repository, config)
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
        Configurator.setLevel(EXPOSED_LOG_NAME, Level.TRACE)
        DataMediator.mediate(repository, config)
        val entities = repository.get(typeManager["import-data"])
        val entities1 = repository.get(typeManager["entity1"])
        val entities2 = repository.get(typeManager["entity2"])
        assertThat(entities).hasSize(4)
        val sku3 = entities.find { it["sku"]?.value == 3 }
        val sku4 = entities.find { it["sku"]?.value == 4 }
        val sku21 = entities.find { it["sku"]?.value == 21 }
        val sku22 = entities.find { it["sku"]?.value == 22 }
        assertThat(sku3).isNotNull
        assertThat(sku3!!["desc"]?.value)
            .isEqualTo(entities2.find { it["sku"]?.value == 3 }!!["desc"]?.value)
        assertThat(sku3["amount"]?.value)
            .isEqualTo(entities2.find { it["sku"]?.value == 3 }!!["amount"]?.value)
        assertThat(sku3["corrected_amount"]?.value)
            .isEqualTo((entities1.find { it["sku"]?.value == 3 }!!["amount"]?.value as Double) * 11.0)

        assertThat(sku4).isNotNull
        assertThat(sku4!!["desc"]?.value)
            .isEqualTo(entities2.find { it["sku"]?.value == 4 }!!["desc"]?.value)
        assertThat(sku4["amount"]?.value)
            .isEqualTo(entities2.find { it["sku"]?.value == 4 }!!["amount"]?.value)
        assertThat(sku4["corrected_amount"]?.value)
            .isEqualTo((entities1.find { it["sku"]?.value == 4 }!!["amount"]?.value as Double) * 11.0)

        assertThat(sku21).isNotNull
        assertThat(sku21!!["desc"]?.value)
            .isEqualTo(entities2.find { it["sku"]?.value == 21 }!!["desc"]?.value)
        assertThat(sku21["amount"]?.value)
            .isEqualTo(entities2.find { it["sku"]?.value == 21 }!!["amount"]?.value)
        assertThat(sku21["corrected_amount"]?.value)
            .isEqualTo((entities2.find { it["sku"]?.value == 21 }!!["amount"]?.value as Double) * 13.0)

        assertThat(sku22).isNotNull
        assertThat(sku22!!["desc"]?.value)
            .isEqualTo(entities2.find { it["sku"]?.value == 22 }!!["desc"]?.value)
        assertThat(sku22["amount"]?.value)
            .isEqualTo(entities2.find { it["sku"]?.value == 22 }!!["amount"]?.value)
        assertThat(sku22["corrected_amount"]?.value)
            .isEqualTo((entities2.find { it["sku"]?.value == 22 }!!["amount"]?.value as Double) * 13.0)

        val importEntities2 = repository.get(typeManager["import-data-union"])
        assertThat(importEntities2).hasSize(4)
        assertThat(importEntities2.map { it["sku"]?.value })
            .containsAnyElementsOf(listOf(21L, 22L, 1L, 2L))
        repository.close()
    }

    @Suppress("ComplexMethod", "LongMethod")
    private fun createConfiguration(): Config {
        return Config.Builder(helper).apply {
            database {
                name("test_lomout")
                server {
                    host("localhost")
                    port(3306)
                    user("root")
                    if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                        password("")
                    } else {
                        password("root")
                    }
                }
            }
            loader {
                files {
                    val testDataDir = System.getenv("TEST_DATA_DIR")
                    file("test-data") { path("$testDataDir/mediator-test-data.xls") }
                }
                entities {
                    entity("entity1", false) {
                        attribute<LONG>("sku") { key() }
                        attribute<STRING>("desc")
                        attribute<DOUBLE>("amount")
                    }
                    entity("entity2", false) {
                        copyFrom("entity1")
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
                productionLine {
                    input {
                        entity("entity1") {
                            statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                            extAttribute<DOUBLE>("corrected_amount") {
                                builder { DOUBLE((it["amount"]!!.value as Double) * 11.0) }
                            }
                        }
                        entity("entity2") {
                            statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                            extAttribute<DOUBLE>("corrected_amount") {
                                builder { DOUBLE((it["amount"]!!.value as Double) * 13.0) }
                            }
                        }
                    }
                    output("import-data") {
                        copyFrom("entity1")
                        attribute<DOUBLE>("corrected_amount")
                    }
                    pipeline {
                        classifier { element ->
                            val entities = element.entities
                            // start test case for pipeline data collection
                            val flag1 = try {
                                entities["unknown"]
                                false
                            } catch (e: AppDataException) {
                                true
                            }
                            val flag2 = entities.getOrNull("unknown") == null &&
                                    entities.getOrNull("entity1") != null
                            // finish test case for pipeline data collection
                            val entityOne = entities.getOrNull("entity1")
                            if (entityOne != null) {
                                val typeTwo = entityTypeManager["entity2"]
                                val entityTwo = repository.get(
                                    typeTwo,
                                    mapOf(typeTwo["sku"] to entityOne["sku"]!!),
                                    *ACTUAL_ENTITY
                                )
                                if (entityTwo != null && flag1 && flag2) {
                                    element.match(entityTwo)
                                } else {
                                    element.mismatch()
                                }
                            } else if (entities.getOrNull("entity2") != null) {
                                val entityTwo = entities["entity2"]
                                val typeOne = entityTypeManager["entity1"]
                                val partner = repository.get(
                                    typeOne,
                                    mapOf(typeOne["sku"] to entityTwo["sku"]!!),
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
                productionLine {
                    input {
                        entity("entity1") {
                            statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                            extAttribute<DOUBLE>("corrected_amount") {
                                builder { DOUBLE((it["amount"]!!.value as Double) * 11.0) }
                            }
                        }
                        entity("entity2") {
                            statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                            extAttribute<DOUBLE>("corrected_amount") {
                                builder { DOUBLE((it["amount"]!!.value as Double) * 13.0) }
                            }
                        }
                    }
                    output("import-data-union") {
                        copyFrom("entity1")
                        attribute<DOUBLE>("corrected_amount")
                    }
                    pipeline {
                        classifier {
                            val entities = it.entities
                            if (entities[0]["sku"]?.value in listOf(21L, 22L, 1L, 2L)) {
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
        }.build()
    }
}
