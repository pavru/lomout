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
import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.callable.AttributeReader
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.callable.Reader
import net.pototskiy.apps.lomout.api.callable.ReaderBuilder
import net.pototskiy.apps.lomout.api.createContext
import net.pototskiy.apps.lomout.api.callable.createReader
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.Key
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.script.LomoutScript
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import java.io.File

@Suppress("MagicNumber")
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
internal class EntityLoaderAddTest {
    private val helper = ScriptBuildHelper()

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun ignoreEmptyRowTest() {
        val script = createConfIgnoreEmptyRow()
        val repository = EntityRepository(script.database, Level.ERROR)
        LomoutContext.setContext(createContext {
            this.script = script
            scriptFile = File("EntityLoaderAddTest.kt")
            this.repository = repository
            logger = LogManager.getLogger(LOADER_LOG_NAME)
        })
        repository.getIDs(EntityLoaderAddEntity1::class).forEach { repository.delete(EntityLoaderAddEntity1::class, it) }
        DataLoader().load()
        val entities = repository.get(EntityLoaderAddEntity1::class)
        assertThat(entities).hasSize(5)
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun stopOnEmptyRowTest() {
        val script = createConfStopEmptyRow()
        val repository = EntityRepository(script.database, Level.ERROR)
        LomoutContext.setContext(createContext {
            this.script = script
            scriptFile = File("EntityLoaderAddTest.kt")
            this.repository = repository
            logger = LogManager.getLogger(LOADER_LOG_NAME)
        })
        repository.getIDs(EntityLoaderAddEntity1::class).forEach { repository.delete(EntityLoaderAddEntity1::class, it) }
        DataLoader().load()
        val entities = repository.get(EntityLoaderAddEntity1::class)
        assertThat(entities).hasSize(3)
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun tryToLoadNullToNotNullTest() {
        val script = createConfWithSecondField()
        val repository = EntityRepository(script.database, Level.ERROR)
        LomoutContext.setContext(createContext {
            this.script = script
            scriptFile = File("EntityLoaderAddTest.kt")
            this.repository = repository
            logger = LogManager.getLogger(LOADER_LOG_NAME)
        })
        repository.getIDs(EntityLoaderAddEntity2::class).forEach { repository.delete(EntityLoaderAddEntity2::class, it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader().load()
        val entities = repository.get(EntityLoaderAddEntity2::class)
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
        val script = createConfZeroDivision()
        val repository = EntityRepository(script.database, Level.ERROR)
        LomoutContext.setContext(createContext {
            this.script = script
            scriptFile = File("EntityLoaderAddTest.kt")
            this.repository = repository
            logger = LogManager.getLogger(LOADER_LOG_NAME)
        })
        repository.getIDs(EntityLoaderAddEntity3::class).forEach { repository.delete(EntityLoaderAddEntity3::class, it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.TRACE)
        DataLoader().load()
        val entities = repository.get(EntityLoaderAddEntity3::class)
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
        val script = createConfWithTwoFieldSets()
        val repository = EntityRepository(script.database, Level.ERROR)
        LomoutContext.setContext(createContext {
            this.script = script
            scriptFile = File("EntityLoaderAddTest.kt")
            this.repository = repository
            logger = LogManager.getLogger(LOADER_LOG_NAME)
        })
        repository.getIDs(EntityLoaderAddEntity1::class).forEach { repository.delete(EntityLoaderAddEntity1::class, it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader().load()
        val entities = repository.get(EntityLoaderAddEntity1::class)
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
        val script = createConfBlankKeyField()
        val repository = EntityRepository(script.database, Level.ERROR)
        LomoutContext.setContext(createContext {
            this.script = script
            scriptFile = File("EntityLoaderAddTest.kt")
            this.repository = repository
            logger = LogManager.getLogger(LOADER_LOG_NAME)
        })
        repository.getIDs(EntityLoaderAddEntity4::class).forEach { repository.delete(EntityLoaderAddEntity4::class, it) }
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataLoader().load()
        val entities = repository.get(EntityLoaderAddEntity4::class)
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
    internal open class EntityLoaderAddEntity1 : Document() {
        @Key
        var key: Long = 0L
        var data: String = ""

        companion object : DocumentMetadata(EntityLoaderAddEntity1::class)
    }

    internal class EntityLoaderAddOutput1 : EntityLoaderAddEntity1() {
        companion object : DocumentMetadata(EntityLoaderAddOutput1::class)
    }

    internal open class EntityLoaderAddEntity2 : Document() {
        @Suppress("unused")
        @Key
        var key: Long = 0L
        @Suppress("unused")
        var data: String = ""
        @Suppress("unused")
        var second: String = ""

        companion object : DocumentMetadata(EntityLoaderAddEntity2::class)
    }

    internal class EntityLoaderAddOutput2 : EntityLoaderAddEntity2() {
        companion object : DocumentMetadata(EntityLoaderAddOutput2::class)
    }

    internal open class EntityLoaderAddEntity3 : Document() {
        @Suppress("unused")
        @Key
        var key: Long = 0L
        @Suppress("unused")
        @Reader(DivByZeroReader::class)
        var data: String = ""

        companion object : DocumentMetadata(EntityLoaderAddEntity3::class)
    }

    internal class EntityLoaderAddOutput3 : EntityLoaderAddEntity3() {
        companion object : DocumentMetadata(EntityLoaderAddOutput3::class)
    }

    internal open class EntityLoaderAddEntity4 : Document() {
        @Suppress("unused")
        @Key
        var key: String = ""
        @Suppress("unused")
        var data: String = ""

        companion object : DocumentMetadata(EntityLoaderAddEntity4::class)
    }

    private fun createConfIgnoreEmptyRow(): LomoutScript {
        return LomoutScript.Builder(helper).apply {
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
                load<EntityLoaderAddEntity1> {
                    fromSources { source { file("test-data"); sheet("default"); ignoreEmptyRows() } }
                    rowsToSkip(1)
                    keepAbsentForDays(1)
                    sourceFields {
                        main("entity") {
                            field("key") { column(0) }
                            field("data") { column(1) } toAttr EntityLoaderAddEntity1::data
                        }
                    }
                }
            }
            mediator {
                produce<EntityLoaderAddOutput1> {
                    input {
                        entity(EntityLoaderAddEntity1::class)
                    }
                    pipeline {
                        assembler { null }
                    }
                }
            }
        }.build()
    }

    private fun createConfStopEmptyRow(): LomoutScript {
        return LomoutScript.Builder(helper).apply {
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
                load<EntityLoaderAddEntity1> {
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
                produce<EntityLoaderAddOutput1> {
                    input {
                        entity(EntityLoaderAddEntity1::class)
                    }
                    pipeline {
                        assembler { null }
                    }
                }
            }
        }.build()
    }

    private fun createConfWithSecondField(): LomoutScript {
        return LomoutScript.Builder(helper).apply {
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
                load<EntityLoaderAddEntity2> {
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
                produce<EntityLoaderAddOutput2> {
                    input {
                        entity(EntityLoaderAddEntity2::class)
                    }
                    pipeline {
                        assembler { null }
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
        override operator fun invoke(
            attribute: DocumentMetadata.Attribute,
            input: Cell,
            context: LomoutContext
        ): String? {
            val v = 1
            val c = v / v - v
            @Suppress("DIVISION_BY_ZERO")
            return "test${v / c}"
        }

        override fun build(): AttributeReader<out Any?> = createReader<DivByZeroReader>()
    }

    private fun createConfZeroDivision(): LomoutScript {
        return LomoutScript.Builder(helper).apply {
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
                load<EntityLoaderAddEntity3> {
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
                produce<EntityLoaderAddOutput3> {
                    input {
                        entity(EntityLoaderAddEntity3::class)
                    }
                    pipeline {
                        assembler { null }
                    }
                }
            }
        }.build()
    }

    private fun createConfBlankKeyField(): LomoutScript {
        return LomoutScript.Builder(helper).apply {
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
                load<EntityLoaderAddEntity4> {
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
                produce<EntityLoaderAddOutput1> {
                    input {
                        entity(EntityLoaderAddEntity4::class)
                    }
                    pipeline {
                        assembler { null }
                    }
                }
            }
        }.build()
    }

    private fun createConfWithTwoFieldSets(): LomoutScript {
        return LomoutScript.Builder(helper).apply {
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
                load<EntityLoaderAddEntity1> {
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
                produce<EntityLoaderAddOutput1> {
                    input {
                        entity(EntityLoaderAddEntity1::class)
                    }
                    pipeline {
                        assembler { null }
                    }
                }
            }
        }.build()
    }
}
