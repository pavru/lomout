package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.LogCatcher
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.LongType
import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.entity.get
import net.pototskiy.apps.magemediation.database.initDatabase
import org.apache.logging.log4j.Level
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

@Suppress("MagicNumber")
internal class EntityLoaderAddTest {
    private val typeManager = EntityTypeManager()
    private val helper = ConfigBuildHelper(typeManager)

    @Test
    internal fun ignoreEmptyRowTest() {
        val config = createConfIgnoreEmptyRow()
        initDatabase(config.database, typeManager)
        transaction { DbEntityTable.deleteAll() }
        DataLoader.load(config)
        val entities = DbEntity.getEntities(typeManager["entity"])
        assertThat(entities).hasSize(5)
    }

    @Test
    internal fun stopOnEmptyRowTest() {
        val config = createConfStopEmptyRow()
        initDatabase(config.database, typeManager)
        transaction { DbEntityTable.deleteAll() }
        DataLoader.load(config)
        val entities = DbEntity.getEntities(typeManager["entity"])
        assertThat(entities).hasSize(3)
    }

    @Test
    internal fun tryToLoadNullToNotNullTest() {
        val config = createConfWithSecondField()
        initDatabase(config.database, typeManager)
        transaction { DbEntityTable.deleteAll() }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader.load(config)
        val entities = DbEntity.getEntities(typeManager["entity"])
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(0)
        @Suppress("RegExpRedundantEscape")
        val matches = Regex(
            "^\\[ERROR\\].*There is no requested cell<3> in row.*$",
            RegexOption.MULTILINE
        )
            .findAll(log).toList()
        assertThat(matches).isNotNull
        assertThat(matches).hasSize(3)
    }

    @Test
    internal fun tryWithWrongReaderTest() {
        val config = createConfZeroDivision()
        initDatabase(config.database, typeManager)
        transaction { DbEntityTable.deleteAll() }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader.load(config)
        val entities = DbEntity.getEntities(typeManager["entity"])
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(0)
        @Suppress("RegExpRedundantEscape")
        val matches = Regex("^\\[ERROR\\].*\\/ by zero.*$", RegexOption.MULTILINE)
            .findAll(log).toList()
        assertThat(matches).isNotNull
        assertThat(matches).hasSize(6)
    }

    @Test
    internal fun tryWithTwoFieldSetsTest() {
        val config = createConfWithTwoFieldSets()
        initDatabase(config.database, typeManager)
        transaction { DbEntityTable.deleteAll() }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader.load(config)
        val entities = DbEntity.getEntities(typeManager["entity"])
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(0)
        @Suppress("RegExpRedundantEscape")
        val matches = Regex(
            "^\\[ERROR\\].*Field<data> does not match required regular expression.*$",
            RegexOption.MULTILINE
        )
            .findAll(log).toList()
        assertThat(matches).isNotNull
        assertThat(matches).hasSize(3)
    }

    @Test
    internal fun tryWithBlankKeyFieldTest() {
        val config = createConfBlankKeyField()
        initDatabase(config.database, typeManager)
        transaction { DbEntityTable.deleteAll() }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader.load(config)
        val entities = DbEntity.getEntities(typeManager["entity"])
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(2)
        val matches = Regex(
            "^\\[ERROR\\].*Attribute<key> is key but has no value.*$",
            RegexOption.MULTILINE
        ).findAll(log).toList()
        assertThat(matches).isNotNull
        assertThat(matches).hasSize(1)
    }

    private fun createConfIgnoreEmptyRow(): Config {
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                entities {
                    entity("entity", false) {
                        attribute<LongType>("key") { key() }
                        attribute<StringType>("data")
                    }
                }
                loadEntity("entity") {
                    fromSources { source { file("test-data"); sheet("default"); ignoreEmptyRows() } }
                    rowsToSkip(1)
                    keepAbsentForDays(1)
                    sourceFields {
                        main("entity") {
                            field("key") { column(0) }
                            field("data") { column(1) }
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                entities {
                    entity("entity", false) {
                        attribute<LongType>("key") { key() }
                        attribute<StringType>("data")
                    }
                }
                loadEntity("entity") {
                    fromSources { source { file("test-data"); sheet("default"); stopOnEmptyRow() } }
                    rowsToSkip(1)
                    keepAbsentForDays(1)
                    sourceFields {
                        main("entity") {
                            field("key") { column(0) }
                            field("data") { column(1) }
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

    private fun createConfWithSecondField(): Config {
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                entities {
                    entity("entity", false) {
                        attribute<LongType>("key") { key() }
                        attribute<StringType>("data")
                        attribute<StringType>("second")
                    }
                }
                loadEntity("entity") {
                    fromSources { source { file("test-data"); sheet("default"); stopOnEmptyRow() } }
                    rowsToSkip(1)
                    keepAbsentForDays(1)
                    sourceFields {
                        main("entity") {
                            field("key") { column(0) }
                            field("data") { column(1) }
                            field("second") { column(2) }
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

    private fun createConfZeroDivision(): Config {
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                entities {
                    entity("entity", false) {
                        attribute<LongType>("key") { key() }
                        attribute<StringType>("data") {
                            reader { _, _ ->
                                val v = 1
                                val c = v / v - v
                                StringType("test${v / c}")
                            }
                        }
                    }
                }
                loadEntity("entity") {
                    fromSources { source { file("test-data"); sheet("default"); stopOnEmptyRow() } }
                    rowsToSkip(1)
                    keepAbsentForDays(1)
                    sourceFields {
                        main("entity") {
                            field("key") { column(0) }
                            field("data") { column(1) }
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

    private fun createConfBlankKeyField(): Config {
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test-blank-key.csv") }
                }
                entities {
                    entity("entity", false) {
                        attribute<StringType>("key") { key() }
                        attribute<StringType>("data")
                    }
                }
                loadEntity("entity") {
                    fromSources { source { file("test-data"); sheet("default"); stopOnEmptyRow() } }
                    rowsToSkip(1)
                    keepAbsentForDays(1)
                    sourceFields {
                        main("entity") {
                            field("key") { column(0) }
                            field("data") { column(1) }
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

    private fun createConfWithTwoFieldSets(): Config {
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                entities {
                    entity("entity", false) {
                        attribute<LongType>("key") { key() }
                        attribute<StringType>("data")
                    }
                }
                loadEntity("entity") {
                    fromSources { source { file("test-data"); sheet("default"); stopOnEmptyRow() } }
                    rowsToSkip(1)
                    keepAbsentForDays(1)
                    sourceFields {
                        main("entity") {
                            field("key") { column(0) }
                            field("data") { column(1); pattern("test[0-9]{2,2}") }
                        }
                        extra("entity-ext") {
                            field("key") { column(0) }
                            field("data") { column(1); pattern("test[0-9]{3,3}") }
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
