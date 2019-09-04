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

import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
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
import net.pototskiy.apps.lomout.api.entity.reader.DocumentAttributeReader
import net.pototskiy.apps.lomout.api.script.LomoutScript
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import java.io.File

@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
internal class NestedFieldLoadingTest {
    private val helper = ScriptBuildHelper()
    @BeforeEach
    internal fun setUp() {
        Configurator.setLevel(ROOT_LOG_NAME, Level.WARN)
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun loadNestedFieldsWithoutErrorsTest() {
        val script = createConfStopEmptyRow()
        val repository = EntityRepository(script.database, Level.ERROR)
        LomoutContext.setContext(createContext {
            this.script = script
            scriptFile = File("NestedFieldLoadingTest.kt")
            this.repository = repository
            logger = LogManager.getLogger(LOADER_LOG_NAME)
        })
        repository.getIDs(NestedFieldLoadingEntity::class).forEach { repository.delete(NestedFieldLoadingEntity::class, it) }
        DataLoader().load()
        @Suppress("UNCHECKED_CAST")
        val entities = repository.get(NestedFieldLoadingEntity::class) as List<NestedFieldLoadingEntity>
        Assertions.assertThat(entities).hasSize(2)
        assertThat(entities[0].key).isEqualTo(1L)
        assertThat(entities[0].parent.parent2.parent3.attr1).isEqualTo("a1")
        assertThat(entities[0].parent.parent2.parent3.attr2).isEqualTo("a2")
        assertThat(entities[0].parent.parent2.attr3).isEqualTo("a3")
        repository.close()
    }

    internal class NestedFieldLoadingNestedOne : Document() {
        @Reader(Parent2Reader::class)
        var parent2: NestedFieldLoadingNestedTwo = NestedFieldLoadingNestedTwo()

        companion object : DocumentMetadata(NestedFieldLoadingNestedOne::class)

        class Parent2Reader : ReaderBuilder {
            override fun build(): AttributeReader<out Any?> = createReader<DocumentAttributeReader> {
                delimiter = '%'
                valueDelimiter = '#'
            }
        }
    }

    internal class NestedFieldLoadingNestedTwo : Document() {
        var attr3: String = ""
        @Reader(Parent3Reader::class)
        var parent3: NestedFieldLoadingNestedThree = NestedFieldLoadingNestedThree()

        companion object : DocumentMetadata(NestedFieldLoadingNestedTwo::class)

        class Parent3Reader : ReaderBuilder {
            override fun build(): AttributeReader<out Any?> = createReader<DocumentAttributeReader> {
                delimiter = '|'
            }
        }
    }

    internal class NestedFieldLoadingNestedThree : Document() {
        var attr1: String = ""
        var attr2: String = ""

        companion object : DocumentMetadata(NestedFieldLoadingNestedThree::class)
    }

    internal open class NestedFieldLoadingEntity : Document() {
        @Key
        var key: Long = 0L
        @Reader(ParentReader::class)
        var parent: NestedFieldLoadingNestedOne = NestedFieldLoadingNestedOne()

        companion object : DocumentMetadata(NestedFieldLoadingEntity::class)

        class ParentReader : ReaderBuilder {
            override fun build(): AttributeReader<out Any?> = createReader<DocumentAttributeReader> {
                delimiter = ','
                valueDelimiter = ':'
            }
        }
    }

    internal class NestedFieldLoadingOutput : NestedFieldLoadingEntity() {
        companion object : DocumentMetadata(NestedFieldLoadingOutput::class)
    }

    @Suppress("LongMethod", "MagicNumber")
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
                    file("test-data") { path("$testDataDir/entity-loader-nested-attribute-test.xls") }
                }
                load<NestedFieldLoadingEntity> {
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
                produce<NestedFieldLoadingOutput> {
                    input {
                        entity(NestedFieldLoadingEntity::class)
                    }
                    pipeline {
                        assembler { NestedFieldLoadingOutput() }
                    }
                }
            }
        }.build()
    }
}
