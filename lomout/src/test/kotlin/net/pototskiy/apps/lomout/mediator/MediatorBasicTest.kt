package net.pototskiy.apps.lomout.mediator

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.Key
import net.pototskiy.apps.lomout.api.document.documentMetadata
import net.pototskiy.apps.lomout.api.entity.EntityRepository
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
import org.litote.kmongo.eq
import java.io.File

@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
@Execution(ExecutionMode.SAME_THREAD)
@Suppress("MagicNumber")
internal class MediatorBasicTest {
    private val helper = ConfigBuildHelper()

    @Suppress("LongMethod")
    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun complexBasicTest() {
        val config = createConfiguration()
        PluginContext.config = config
        PluginContext.scriptFile = File("no-file.conf.kts")

        System.setProperty("mediation.line.cache.size", "4")
        val repository = EntityRepository(config.database, Level.ERROR)
        PluginContext.repository = repository
        repository.getIDs(Entity1::class).forEach { repository.delete(Entity1::class, it) }
        repository.getIDs(Entity2::class).forEach { repository.delete(Entity2::class, it) }
        repository.getIDs(ImportData::class).forEach { repository.delete(ImportData::class, it) }
        repository.getIDs(ImportDataUnion::class).forEach { repository.delete(ImportDataUnion::class, it) }

        DataLoader.load(repository, config)
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
        Configurator.setLevel(EXPOSED_LOG_NAME, Level.TRACE)
        DataMediator.mediate(repository, config)
        @Suppress("UNCHECKED_CAST")
        val entities = repository.get(ImportData::class) as List<ImportData>
        @Suppress("UNCHECKED_CAST")
        val entities1 = repository.get(Entity1::class) as List<Entity1>
        @Suppress("UNCHECKED_CAST")
        val entities2 = repository.get(Entity2::class) as List<Entity2>
        assertThat(entities).hasSize(4)
        val sku3 = entities.find { it.sku == 3L }
        val sku4 = entities.find { it.sku == 4L }
        val sku21 = entities.find { it.sku == 21L }
        val sku22 = entities.find { it.sku == 22L }
        assertThat(sku3).isNotNull
        assertThat(sku3!!.desc).isEqualTo(entities2.find { it.sku == 3L }!!.desc)
        assertThat(sku3.amount).isEqualTo(entities2.find { it.sku == 3L }!!.amount)
        assertThat(sku3.corrected_amount).isEqualTo(entities1.find { it.sku == 3L }!!.amount * 11.0)

        assertThat(sku4).isNotNull
        assertThat(sku4!!.desc).isEqualTo(entities2.find { it.sku == 4L }!!.desc)
        assertThat(sku4.amount).isEqualTo(entities2.find { it.sku == 4L }!!.amount)
        assertThat(sku4.corrected_amount).isEqualTo(entities1.find { it.sku == 4L }!!.amount * 11.0)

        assertThat(sku21).isNotNull
        assertThat(sku21!!.desc).isEqualTo(entities2.find { it.sku == 21L }!!.desc)
        assertThat(sku21.amount).isEqualTo(entities2.find { it.sku == 21L }!!.amount)
        assertThat(sku21.corrected_amount).isEqualTo(entities2.find { it.sku == 21L }!!.amount * 13.0)

        assertThat(sku22).isNotNull
        assertThat(sku22!!.desc).isEqualTo(entities2.find { it.sku == 22L }!!.desc)
        assertThat(sku22.amount).isEqualTo(entities2.find { it.sku == 22L }!!.amount)
        assertThat(sku22.corrected_amount).isEqualTo(entities2.find { it.sku == 22L }!!.amount * 13.0)

        @Suppress("UNCHECKED_CAST") val importEntities2 = repository.get(ImportDataUnion::class) as List<ImportDataUnion>
        assertThat(importEntities2).hasSize(4)
        assertThat(importEntities2.map { it.sku })
            .containsAnyElementsOf(listOf(21L, 22L, 1L, 2L))
        repository.close()
    }

    @Suppress("PropertyName")
    internal open class Entity1 : Document() {
        @Key
        var sku: Long = 0L
        var desc: String = ""
        var amount: Double = 0.0
        open val corrected_amount: Double
            get() = amount * 11.0

        companion object : DocumentMetadata(Entity1::class)
    }

    internal class Entity2 : Entity1() {
        override val corrected_amount: Double
            get() = amount * 13.0

        companion object : DocumentMetadata(Entity2::class)
    }

    internal class ImportData : Entity1() {
        override var corrected_amount: Double = 0.0

        companion object : DocumentMetadata(ImportData::class)
    }

    internal class ImportDataUnion : Entity1() {
        override var corrected_amount: Double = 0.0

        companion object : DocumentMetadata(ImportDataUnion::class)
    }

    internal class Unknown : Document() {
        companion object : DocumentMetadata(Unknown::class)
    }

    @Suppress("ComplexMethod", "LongMethod")
    private fun createConfiguration(): Config {
        return Config.Builder(helper).apply {
            database {
                name("lomout_test")
                server {
                    host("localhost")
                    port(27017)
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
                loadEntity(Entity1::class) {
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
                loadEntity(Entity2::class) {
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
                        entity(Entity1::class)
                        entity(Entity2::class)
                    }
                    output(ImportData::class)
                    pipeline {
                        classifier { element ->
                            val entities = element.entities
                            // start test case for pipeline data collection
                            val flag1 = try {
                                entities[Unknown::class]
                                false
                            } catch (e: AppDataException) {
                                true
                            }
                            val flag2 = entities.getOrNull(Unknown::class) == null &&
                                    entities.getOrNull(Entity1::class) != null
                            // finish test case for pipeline data collection
                            val entityOne = entities.getOrNull(Entity1::class)
                            if (entityOne != null) {
                                val typeTwo = Entity2::class
                                val entityTwo = repository.get(
                                    typeTwo,
                                    Entity2::sku eq entityOne.getAttribute("sku")!!
                                )
                                if (entityTwo != null && flag1 && flag2) {
                                    element.match(entityTwo)
                                } else {
                                    element.mismatch()
                                }
                            } else if (entities.getOrNull(Entity2::class) != null) {
                                val entityTwo = entities[Entity2::class]
                                val typeOne = Entity1::class
                                val partner = repository.get(
                                    typeOne,
                                    Entity1::sku eq entityTwo.getAttribute("sku")!!
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
                                val attrs = target.documentMetadata.attributes
                                mapOf(
                                    attrs.getValue("sku") to entities[0].getAttribute("sku")!!,
                                    attrs.getValue("desc") to entities[1].getAttribute("desc")!!,
                                    attrs.getValue("amount") to entities[1].getAttribute("amount")!!,
                                    attrs.getValue("corrected_amount") to (entities[0] as Entity1).corrected_amount
                                )
                            }
                        }
                        pipeline(Pipeline.CLASS.UNMATCHED) {
                            classifier {
                                val entities = it.entities
                                if (entities[0].documentMetadata.klass == Entity2::class) {
                                    it.match()
                                } else {
                                    it.mismatch()
                                }
                            }
                            assembler { target, entities ->
                                val attrs = target.documentMetadata.attributes
                                mapOf(
                                    attrs.getValue("sku") to entities[0].getAttribute("sku")!!,
                                    attrs.getValue("desc") to entities[0].getAttribute("desc")!!,
                                    attrs.getValue("amount") to entities[0].getAttribute("amount")!!,
                                    attrs.getValue("corrected_amount") to (entities[0] as Entity2).corrected_amount
                                )
                            }
                        }
                    }
                }
                productionLine {
                    input {
                        entity(Entity1::class)
                        entity(Entity2::class)
                    }
                    output(ImportDataUnion::class)
                    pipeline {
                        classifier {
                            val entities = it.entities
                            if (entities[0].getAttribute("sku") in listOf(21L, 22L, 1L, 2L)) {
                                it.match()
                            } else {
                                it.mismatch()
                            }
                        }
                        assembler { target, entities ->
                            val attrs = target.documentMetadata.attributes
                            mapOf(
                                attrs.getValue("sku") to entities[0].getAttribute("sku")!!,
                                attrs.getValue("desc") to entities[0].getAttribute("desc")!!,
                                attrs.getValue("amount") to entities[0].getAttribute("amount")!!,
                                attrs.getValue("corrected_amount") to if (entities[0] is Entity1) {
                                    (entities[0] as Entity1).corrected_amount
                                } else {
                                    (entities[0] as Entity2).corrected_amount
                                }

                            )
                        }
                    }
                }
            }
        }.build()
    }
}
