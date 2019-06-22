package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.LogCatcher
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.get
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.STRING
import org.apache.logging.log4j.Level
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock


@Suppress("MagicNumber")
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
internal class EntityLoaderAddTest {
    private val typeManager = EntityTypeManagerImpl()
    private val helper = ConfigBuildHelper(typeManager)

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun ignoreEmptyRowTest() {
        val config = createConfIgnoreEmptyRow()
        val repository = EntityRepository(config.database, typeManager, Level.ERROR)
        repository.getIDs(typeManager["entity"]).forEach { repository.delete(it) }
        DataLoader.load(repository, config)
        val entities = repository.get(typeManager["entity"])
        assertThat(entities).hasSize(5)
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun stopOnEmptyRowTest() {
        val config = createConfStopEmptyRow()
        val repository = EntityRepository(config.database, typeManager, Level.ERROR)
        repository.getIDs(typeManager["entity"]).forEach { repository.delete(it) }
        DataLoader.load(repository, config)
        val entities = repository.get(typeManager["entity"])
        assertThat(entities).hasSize(3)
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun tryToLoadNullToNotNullTest() {
        val config = createConfWithSecondField()
        val repository = EntityRepository(config.database, typeManager, Level.ERROR)
        repository.getIDs(typeManager["entity"]).forEach { repository.delete(it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader.load(repository, config)
        val entities = repository.get(typeManager["entity"])
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
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun tryWithWrongReaderTest() {
        val config = createConfZeroDivision()
        val repository = EntityRepository(config.database, typeManager, Level.ERROR)
        repository.getIDs(typeManager["entity"]).forEach { repository.delete(it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.TRACE)
        DataLoader.load(repository, config)
        val entities = repository.get(typeManager["entity"])
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(0)
        @Suppress("RegExpRedundantEscape")
        val matches = Regex("""^\[(ERROR|TRACE)\].*\/ by zero.*$""", RegexOption.MULTILINE)
            .findAll(log).toList()
        assertThat(matches).isNotNull
        assertThat(matches).hasSize(6)
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun tryWithTwoFieldSetsTest() {
        val config = createConfWithTwoFieldSets()
        val repository = EntityRepository(config.database, typeManager, Level.ERROR)
        repository.getIDs(typeManager["entity"]).forEach { repository.delete(it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader.load(repository, config)
        val entities = repository.get(typeManager["entity"])
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
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun tryWithBlankKeyFieldTest() {
        val config = createConfBlankKeyField()
        val repository = EntityRepository(config.database, typeManager, Level.ERROR)
        repository.getIDs(typeManager["entity"]).forEach { repository.delete(it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader.load(repository, config)
        val entities = repository.get(typeManager["entity"])
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(2)
        @Suppress("RegExpRedundantEscape", "GraziInspection") val matches = Regex(
            "^\\[ERROR\\].*Attribute is key but has no value. Place: .*$",
            RegexOption.MULTILINE
        ).findAll(log).toList()
        assertThat(matches).isNotNull
        assertThat(matches).hasSize(1)
        repository.close()
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
                        attribute<LONG>("key") { key() }
                        attribute<STRING>("data")
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
                        copyFrom("entity")
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
                        attribute<LONG>("key") { key() }
                        attribute<STRING>("data")
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
                        copyFrom("entity")
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
                        attribute<LONG>("key") { key() }
                        attribute<STRING>("data")
                        attribute<STRING>("second")
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
                        copyFrom("entity")
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
                        attribute<LONG>("key") { key() }
                        attribute<STRING>("data") {
                            reader { _, _ ->
                                val v = 1
                                val c = v / v - v
                                @Suppress("DIVISION_BY_ZERO")
                                (STRING("test${v / c}"))
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
                        copyFrom("entity")
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
                        attribute<STRING>("key") { key() }
                        attribute<STRING>("data")
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
                        copyFrom("entity")
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
                        attribute<LONG>("key") { key() }
                        attribute<STRING>("data")
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
                        copyFrom("entity")
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
