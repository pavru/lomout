package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.ROOT_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.LongType
import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.entity.get
import net.pototskiy.apps.magemediation.api.entity.reader.AttributeListReader
import net.pototskiy.apps.magemediation.database.initDatabase
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NestedFieldLoadingTest {
    private val typeManager = EntityTypeManager()
    private val helper = ConfigBuildHelper(typeManager)
    @BeforeEach
    internal fun setUp() {
        Configurator.setLevel(ROOT_LOG_NAME, Level.WARN)
    }

    @Test
    internal fun loadNestedFieldsWithoutErrorsTest() {
        val config = createConfStopEmptyRow()
        initDatabase(config.database, typeManager)
        transaction { DbEntityTable.deleteAll() }
        DataLoader.load(config)
        val entities = DbEntity.getEntities(typeManager["entity"], true)
        val type = entities[0].entityType
        Assertions.assertThat(entities).hasSize(2)
        assertThat(entities[0].data[type["parent"]]).isNull()
        assertThat(entities[0].data[type["parent2"]]).isNull()
        assertThat(entities[0].data[type["parent3"]]).isNull()
        assertThat(entities[0].data[type["attr1"]]?.value).isEqualTo("a1")
        assertThat(entities[0].data[type["attr2"]]?.value).isEqualTo("a2")
        assertThat(entities[0].data[type["attr3"]]?.value).isEqualTo("a3")
    }

    private fun createConfStopEmptyRow(): Config {
        return Config.Builder(helper).apply {
            database {
                server {
                    host("localhost")
                    port(3306)
                    user("root")
                    if (System.getenv("TRAVIS_BUILD_DIR") == null) {
                        password("root")
                    } else {
                        password("")
                    }
                }
            }
            loader {
                files {
                    val testDataDir = System.getenv("TEST_DATA_DIR")
                    file("test-data") { path("$testDataDir/entity-loader-nested-attribute-test.xls") }
                }
                entities {
                    entity("entity", false) {
                        attribute<LongType>("key") { key() }
                        attribute<AttributeListType>("parent") {
                            reader<AttributeListReader> {
                                delimiter = ','
                                valueDelimiter = ':'
                            }
                        }
                        attribute<AttributeListType>("parent2") {
                            reader<AttributeListReader> {
                                delimiter = '%'
                                valueDelimiter = '#'
                            }
                        }
                        attribute<AttributeListType>("parent3") {
                            reader<AttributeListReader> {
                                delimiter = '|'
                            }
                        }
                        attribute<StringType>("attr1")
                        attribute<StringType>("attr2")
                        attribute<StringType>("attr3")
                    }
                }
                loadEntity("entity") {
                    fromSources { source { file("test-data"); sheet("Sheet1"); stopOnEmptyRow() } }
                    rowsToSkip(1)
                    keepAbsentForDays(1)
                    sourceFields {
                        main("entity") {
                            field("key") { column(0) }
                            field("parent") { column(1) }
                            field("parent2") { parent("parent") }
                            field("parent3") { parent("parent2") }
                            field("attr1") { parent("parent3") }
                            field("attr2") { parent("parent3") }
                            field("attr3") { parent("parent2") }
                        }
                    }
                }
            }
            mediator {
                crossProductionLine {
                    output("output") {
                        inheritFrom("entity")
                    }
                    input {
                        entity("entity")
                    }
                    pipeline {
                        assembler { _, _ -> emptyMap() }
                    }
                }
            }
        }.build()
    }
}
