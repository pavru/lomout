package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.get
import net.pototskiy.apps.lomout.api.entity.reader.AttributeListReader
import net.pototskiy.apps.lomout.api.entity.type.ATTRIBUTELIST
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.STRING
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NestedFieldLoadingTest {
    private val typeManager = EntityTypeManagerImpl()
    private val helper = ConfigBuildHelper(typeManager)
    @BeforeEach
    internal fun setUp() {
        Configurator.setLevel(ROOT_LOG_NAME, Level.WARN)
    }

    @Test
    internal fun loadNestedFieldsWithoutErrorsTest() {
        val config = createConfStopEmptyRow()
        val repository = EntityRepository(config.database, typeManager, Level.ERROR)
        repository.getIDs(typeManager["entity"]).forEach { repository.delete(it) }
        DataLoader.load(repository, config)
        val entities = repository.get(typeManager["entity"])
        val type = entities[0].type
        Assertions.assertThat(entities).hasSize(2)
        assertThat(entities[0].data[type["parent"]]).isNull()
        assertThat(entities[0].data[type["parent2"]]).isNull()
        assertThat(entities[0].data[type["parent3"]]).isNull()
        assertThat(entities[0].data[type["attr1"]]?.value).isEqualTo("a1")
        assertThat(entities[0].data[type["attr2"]]?.value).isEqualTo("a2")
        assertThat(entities[0].data[type["attr3"]]?.value).isEqualTo("a3")
        repository.close()
    }

    @Suppress("LongMethod")
    private fun createConfStopEmptyRow(): Config {
        return Config.Builder(helper).apply {
            database {
                name("test_lomout")
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
                        attribute<LONG>("key") { key() }
                        attribute<ATTRIBUTELIST>("parent") {
                            reader<AttributeListReader> {
                                delimiter = ','
                                valueDelimiter = ':'
                            }
                        }
                        attribute<ATTRIBUTELIST>("parent2") {
                            reader<AttributeListReader> {
                                delimiter = '%'
                                valueDelimiter = '#'
                            }
                        }
                        attribute<ATTRIBUTELIST>("parent3") {
                            reader<AttributeListReader> {
                                delimiter = '|'
                            }
                        }
                        attribute<STRING>("attr1")
                        attribute<STRING>("attr2")
                        attribute<STRING>("attr3")
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
                productionLine {
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
