package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.Key
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.reader.DocumentAttributeReader
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.plugable.Reader
import net.pototskiy.apps.lomout.api.plugable.ReaderBuilder
import net.pototskiy.apps.lomout.api.plugable.createReader
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock

@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
internal class NestedFieldLoadingTest {
    private val helper = ConfigBuildHelper()
    @BeforeEach
    internal fun setUp() {
        Configurator.setLevel(ROOT_LOG_NAME, Level.WARN)
    }

    @Test
    internal fun loadNestedFieldsWithoutErrorsTest() {
        val config = createConfStopEmptyRow()
        val repository = EntityRepository(config.database, Level.ERROR)
        repository.getIDs(Entity::class).forEach { repository.delete(Entity::class, it) }
        DataLoader.load(repository, config)
        @Suppress("UNCHECKED_CAST")
        val entities = repository.get(Entity::class) as List<Entity>
        Assertions.assertThat(entities).hasSize(2)
        assertThat(entities[0].key).isEqualTo(1L)
        assertThat(entities[0].parent.parent2.parent3.attr1).isEqualTo("a1")
        assertThat(entities[0].parent.parent2.parent3.attr2).isEqualTo("a2")
        assertThat(entities[0].parent.parent2.attr3).isEqualTo("a3")
        repository.close()
    }

    internal class NestedOne : Document() {
        @Reader(Parent2Reader::class)
        var parent2: NestedTwo = NestedTwo()

        companion object : DocumentMetadata(NestedOne::class)

        class Parent2Reader : ReaderBuilder {
            override fun build(): AttributeReader<out Any?> = createReader<DocumentAttributeReader> {
                delimiter = '%'
                valueDelimiter = '#'
            }
        }
    }

    internal class NestedTwo : Document() {
        var attr3: String = ""
        @Reader(Parent3Reader::class)
        var parent3: NestedThree = NestedThree()

        companion object : DocumentMetadata(NestedTwo::class)

        class Parent3Reader : ReaderBuilder {
            override fun build(): AttributeReader<out Any?> = createReader<DocumentAttributeReader> {
                delimiter = '|'
            }
        }
    }

    internal class NestedThree : Document() {
        var attr1: String = ""
        var attr2: String = ""

        companion object : DocumentMetadata(NestedThree::class)
    }

    internal open class Entity : Document() {
        @Key
        var key: Long = 0L
        @Reader(ParentReader::class)
        var parent: NestedOne = NestedOne()

        companion object : DocumentMetadata(Entity::class)

        class ParentReader : ReaderBuilder {
            override fun build(): AttributeReader<out Any?> = createReader<DocumentAttributeReader> {
                delimiter = ','
                valueDelimiter = ':'
            }
        }
    }

    internal class Output : Entity() {
        companion object : DocumentMetadata(Output::class)
    }

    @Suppress("LongMethod")
    private fun createConfStopEmptyRow(): Config {
        return Config.Builder(helper).apply {
            database {
                name("lomout_test")
                server {
                    host("localhost")
                    port(27017)
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
                loadEntity(Entity::class) {
                    fromSources { source { file("test-data"); sheet("Sheet1"); stopOnEmptyRow() } }
                    rowsToSkip(1)
                    keepAbsentForDays(1)
                    sourceFields {
                        main("entity") {
                            field("key") { column(0) }
                            field("parent") { column(1) }
                        }
                    }
                }
            }
            mediator {
                productionLine {
                    output(Output::class)
                    input {
                        entity(Entity::class)
                    }
                    pipeline {
                        assembler { _, _ -> emptyMap() }
                    }
                }
            }
        }.build()
    }
}
