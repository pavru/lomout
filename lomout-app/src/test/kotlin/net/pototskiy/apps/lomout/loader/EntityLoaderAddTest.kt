/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.LogCatcher
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.Key
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.plugable.Reader
import net.pototskiy.apps.lomout.api.plugable.ReaderBuilder
import net.pototskiy.apps.lomout.api.plugable.createReader
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.apache.logging.log4j.Level
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock

@Suppress("MagicNumber")
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
internal class EntityLoaderAddTest {
    private val helper = ConfigBuildHelper()

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun ignoreEmptyRowTest() {
        val config = createConfIgnoreEmptyRow()
        val repository = EntityRepository(config.database, Level.ERROR)
        repository.getIDs(Entity1::class).forEach { repository.delete(Entity1::class, it) }
        DataLoader.load(repository, config)
        val entities = repository.get(Entity1::class)
        assertThat(entities).hasSize(5)
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun stopOnEmptyRowTest() {
        val config = createConfStopEmptyRow()
        val repository = EntityRepository(config.database, Level.ERROR)
        repository.getIDs(Entity1::class).forEach { repository.delete(Entity1::class, it) }
        DataLoader.load(repository, config)
        val entities = repository.get(Entity1::class)
        assertThat(entities).hasSize(3)
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun tryToLoadNullToNotNullTest() {
        val config = createConfWithSecondField()
        val repository = EntityRepository(config.database, Level.ERROR)
        repository.getIDs(Entity2::class).forEach { repository.delete(Entity2::class, it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader.load(repository, config)
        val entities = repository.get(Entity2::class)
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(0)
        @Suppress("RegExpRedundantEscape", "GraziInspection")
        val matches = Regex(
            """^.*\[ERROR].*There is no requested cell. Location: .*$""",
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
        val repository = EntityRepository(config.database, Level.ERROR)
        repository.getIDs(Entity3::class).forEach { repository.delete(Entity3::class, it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.TRACE)
        DataLoader.load(repository, config)
        val entities = repository.get(Entity3::class)
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(0)
        val matches = Regex("""^.*\[ERROR].*/ by zero.*$""", RegexOption.MULTILINE)
            .findAll(log).toList()
        assertThat(matches).isNotNull
        assertThat(matches).hasSize(3)
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun tryWithTwoFieldSetsTest() {
        val config = createConfWithTwoFieldSets()
        val repository = EntityRepository(config.database, Level.ERROR)
        repository.getIDs(Entity1::class).forEach { repository.delete(Entity1::class, it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader.load(repository, config)
        val entities = repository.get(Entity1::class)
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(0)
        @Suppress("RegExpRedundantEscape", "GraziInspection")
        val matches = Regex(
            """^.*\[ERROR].*Field does not match required regular expression. Location: .*$""",
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
        val repository = EntityRepository(config.database, Level.ERROR)
        repository.getIDs(Entity4::class).forEach { repository.delete(Entity4::class, it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader.load(repository, config)
        val entities = repository.get(Entity4::class)
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(entities).hasSize(2)
        @Suppress("RegExpRedundantEscape", "GraziInspection") val matches = Regex(
            """^.*\[ERROR].*Attribute is key but has no value. Location: .*$""",
            RegexOption.MULTILINE
        ).findAll(log).toList()
        assertThat(matches).isNotNull
        assertThat(matches).hasSize(1)
        repository.close()
    }

    @Suppress("unused")
    internal open class Entity1 : Document() {
        @Key
        var key: Long = 0L
        var data: String = ""

        companion object : DocumentMetadata(Entity1::class)
    }

    internal class Output1 : Entity1() {
        companion object : DocumentMetadata(Output1::class)
    }

    internal open class Entity2 : Document() {
        @Suppress("unused")
        @Key
        var key: Long = 0L
        @Suppress("unused")
        var data: String = ""
        @Suppress("unused")
        var second: String = ""

        companion object : DocumentMetadata(Entity2::class)
    }

    internal class Output2 : Entity2() {
        companion object : DocumentMetadata(Output2::class)
    }

    internal open class Entity3 : Document() {
        @Suppress("unused")
        @Key
        var key: Long = 0L
        @Suppress("unused")
        @Reader(DivByZeroReader::class)
        var data: String = ""

        companion object : DocumentMetadata(Entity3::class)
    }

    internal class Output3 : Entity3() {
        companion object : DocumentMetadata(Output3::class)
    }

    internal open class Entity4 : Document() {
        @Suppress("unused")
        @Key
        var key: String = ""
        @Suppress("unused")
        var data: String = ""

        companion object : DocumentMetadata(Entity4::class)
    }

    private fun createConfIgnoreEmptyRow(): Config {
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                loadEntity(Entity1::class) {
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
                    output(Output1::class)
                    input {
                        entity(Entity1::class)
                    }
                    pipeline {
                        assembler { Document.emptyDocument }
                    }
                }
            }
        }.build()
    }

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
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                loadEntity(Entity1::class) {
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
                    output(Output1::class)
                    input {
                        entity(Entity1::class)
                    }
                    pipeline {
                        assembler { Document.emptyDocument }
                    }
                }
            }
        }.build()
    }

    private fun createConfWithSecondField(): Config {
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                loadEntity(Entity2::class) {
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
                    output(Output2::class)
                    input {
                        entity(Entity2::class)
                    }
                    pipeline {
                        assembler { Document.emptyDocument }
                    }
                }
            }
        }.build()
    }

    internal class DivByZeroReader : AttributeReader<String?>(), ReaderBuilder {
        /**
         * Reader function
         *
         * @param attribute Attribute<out T> The attribute to read
         * @param input Cell The cell to read attribute value
         * @return T? The read value
         */
        override fun read(attribute: DocumentMetadata.Attribute, input: Cell): String? {
            val v = 1
            val c = v / v - v
            @Suppress("DIVISION_BY_ZERO")
            return "test${v / c}"
        }

        override fun build(): AttributeReader<out Any?> = createReader<DivByZeroReader>()
    }

    private fun createConfZeroDivision(): Config {
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                loadEntity(Entity3::class) {
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
                    output(Output3::class)
                    input {
                        entity(Entity3::class)
                    }
                    pipeline {
                        assembler { Document.emptyDocument }
                    }
                }
            }
        }.build()
    }

    private fun createConfBlankKeyField(): Config {
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test-blank-key.csv") }
                }
                loadEntity(Entity4::class) {
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
                    output(Output1::class)
                    input {
                        entity(Entity4::class)
                    }
                    pipeline {
                        assembler { Document.emptyDocument }
                    }
                }
            }
        }.build()
    }

    private fun createConfWithTwoFieldSets(): Config {
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
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                loadEntity(Entity1::class) {
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
                    output(Output1::class)
                    input {
                        entity(Entity1::class)
                    }
                    pipeline {
                        assembler { Document.emptyDocument }
                    }
                }
            }
        }.build()
    }
}
