package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.LogCatcher
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.entity.StringType
import net.pototskiy.apps.lomout.api.entity.get
import net.pototskiy.apps.lomout.database.initDatabase
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
        @Suppress("RegExpRedundantEscape", "GraziInspection")
        val matches = Regex(
            "^\\[ERROR\\].*There is no requested cell. Place: .*$",
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
        catcher.startToCatch(Level.OFF, Level.TRACE)
        DataLoader.load(config)
        val entities = DbEntity.getEntities(typeManager["entity"])
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(0)
        @Suppress("RegExpRedundantEscape")
        val matches = Regex("""^\[(ERROR|TRACE)\].*\/ by zero.*$""", RegexOption.MULTILINE)
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
        @Suppress("RegExpRedundantEscape", "GraziInspection")
        val matches = Regex(
            "^\\[ERROR\\].*Field does not match required regular expression. Place: .*$",
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
        @Suppress("RegExpRedundantEscape", "GraziInspection") val matches = Regex(
            "^\\[ERROR\\].*Attribute is key but has no value. Place: .*$",
            RegexOption.MULTILINE
        ).findAll(log).toList()
        assertThat(matches).isNotNull
        assertThat(matches).hasSize(1)
    }

    private fun createConfIgnoreEmptyRow(): Config {
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
                                @Suppress("DIVISION_BY_ZERO")
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
