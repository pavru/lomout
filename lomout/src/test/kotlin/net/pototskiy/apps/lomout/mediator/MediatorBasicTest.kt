package net.pototskiy.apps.lomout.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.config.pipeline.ClassifierElement
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.database.EntityLongs
import net.pototskiy.apps.lomout.api.database.EntityStatus
import net.pototskiy.apps.lomout.api.entity.DoubleType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.entity.StringType
import net.pototskiy.apps.lomout.api.entity.get
import net.pototskiy.apps.lomout.api.entity.values.stringToDouble
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.database.initDatabase
import net.pototskiy.apps.lomout.loader.DataLoader
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
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
    private val typeManager = EntityTypeManager()
    private val helper = ConfigBuildHelper(typeManager)

    @Test
    internal fun complexBasicTest() {
        val config = createConfiguration()
        PluginContext.config = config
        PluginContext.entityTypeManager = config.entityTypeManager
        PluginContext.scriptFile = File("no-file.conf.kts")

        System.setProperty("mediation.line.cache.size", "4")
        initDatabase(config.database, typeManager)
        transaction { DbEntityTable.deleteAll() }
        DataLoader.load(config)
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
        Configurator.setLevel(EXPOSED_LOG_NAME, Level.TRACE)
        DataMediator.mediate(config)
        val entities = DbEntity.getEntities(typeManager["import-data"], true)
        val entities1 = DbEntity.getEntities(typeManager["entity1"], true)
        val entities2 = DbEntity.getEntities(typeManager["entity2"], true)
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

        val importEntities2 = DbEntity.getEntities(typeManager["import-data-union"], true)
        assertThat(importEntities2).hasSize(4)
        assertThat(importEntities2.map { it["sku"]?.value })
            .containsAnyElementsOf(listOf(21L, 22L, 1L, 2L))
    }

    @Suppress("ComplexMethod")
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
                        attribute<LongType>("sku") { key() }
                        attribute<StringType>("desc")
                        attribute<DoubleType>("amount")
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
                productionLine {
                    input {
                        entity("entity1") {
                            filter {
                                with(DbEntityTable) {
                                    it[currentStatus] neq EntityStatus.REMOVED
                                }
                            }
                            extAttribute<DoubleType>("corrected_amount", "amount") {
                                reader { _, cell ->
                                    DoubleType(cell.stringValue.stringToDouble("en_US".createLocale()) * 11.0)
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
                                    DoubleType(cell.stringValue.stringToDouble("en_US".createLocale()) * 13.0)
                                }
                            }
                        }
                    }
                    output("import-data") {
                        inheritFrom("entity1")
                        attribute<DoubleType>("corrected_amount")
                    }
                    pipeline {
                        classifier { element ->
                            val entityOneAlias = DbEntityTable.alias("entityOne")
                            val entityTwoAlias = DbEntityTable.alias("entityTwo")
                            val skuOneAlias = EntityLongs.alias("skuOne")
                            val skuTwoAlias = EntityLongs.alias("skuTwo")
                            val entityOneType = entityTypeManager["entity1"]
                            val entityTwoType = entityTypeManager["entity2"]
                            val partnerEntityType = when (element.ids[0].type) {
                                entityOneType -> entityTwoType
                                else -> entityOneType
                            }

                            val v = transaction {
                                entityOneAlias
                                    .join(
                                        skuOneAlias,
                                        JoinType.INNER,
                                        entityOneAlias[DbEntityTable.id],
                                        skuOneAlias[EntityLongs.owner],
                                        additionalConstraint = { skuOneAlias[EntityLongs.code] eq "sku" })
                                    .join(
                                        skuTwoAlias,
                                        JoinType.INNER,
                                        skuOneAlias[EntityLongs.value],
                                        skuTwoAlias[EntityLongs.value],
                                        additionalConstraint = { skuTwoAlias[EntityLongs.code] eq "sku" }
                                    )
                                    .join(entityTwoAlias,
                                        JoinType.INNER,
                                        skuTwoAlias[EntityLongs.owner],
                                        entityTwoAlias[DbEntityTable.id],
                                        additionalConstraint = { entityTwoAlias[DbEntityTable.entityType] eq partnerEntityType }
                                    )
                                    .slice(entityOneAlias[DbEntityTable.id], entityTwoAlias[DbEntityTable.id])
                                    .select {
                                        (entityOneAlias[DbEntityTable.id] eq element.ids[0].id) and
                                                (entityOneAlias[DbEntityTable.id] neq entityTwoAlias[DbEntityTable.id])
                                    }
                                    .toList()
                                    .map {
                                        listOf(
                                            it[entityOneAlias[DbEntityTable.id]],
                                            it[entityTwoAlias[DbEntityTable.id]]
                                        )
                                    }
                            }
                            val entities = element.entities
                            // start test case for pipeline data collection
                            val flag1 = try {
                                entities["unknown"]
                                false
                            } catch (e: AppConfigException) {
                                true
                            }
                            val flag2 = entities.getEntityOrNull("unknown") == null &&
                                    entities.getEntityOrNull("entity1") != null
                            // finish test case for pipeline data collection
                            if (v.isNotEmpty() && element.ids[0].type == entityOneType && flag1 && flag2) {
                                element.match(ClassifierElement.ElementID(entityTwoType, v[0][1]))
                            } else if (v.isNotEmpty()) {
                                element.skip()
                            } else {
                                element.mismatch()
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
                            classifier {
                                val entities = it.entities
                                if (entities[0].entity.entityType.name == "entity2") {
                                    it.match()
                                } else {
                                    it.mismatch()
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
                productionLine {
                    input {
                        entity("entity1") {
                            filter {
                                with(DbEntityTable) {
                                    it[currentStatus] neq EntityStatus.REMOVED
                                }
                            }
                            extAttribute<DoubleType>("corrected_amount", "amount") {
                                reader { _, cell ->
                                    DoubleType(cell.stringValue.stringToDouble("en_US".createLocale()) * 11.0)
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
                                    DoubleType(cell.stringValue.stringToDouble("en_US".createLocale()) * 13.0)
                                }
                            }
                        }
                    }
                    output("import-data-union") {
                        inheritFrom("entity1")
                        attribute<DoubleType>("corrected_amount")
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
                                target["sku"] to entities[0]["sku"],
                                target["desc"] to entities[0]["desc"],
                                target["amount"] to entities[0]["amount"],
                                target["corrected_amount"] to entities[0]["corrected_amount"]
                            )
                        }
                    }
                }
            }
        }.build()
    }
}
