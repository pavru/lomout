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

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.createContext
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.Documents
import net.pototskiy.apps.lomout.api.document.documentMetadata
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.script.EmptyRowBehavior
import net.pototskiy.apps.lomout.api.script.LomoutScript
import net.pototskiy.apps.lomout.api.script.loader.Load
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import java.io.File
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Suppress("MagicNumber")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Loading entity from source file")
@Execution(ExecutionMode.SAME_THREAD)
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
internal class DataLoadingTest {
    private lateinit var lomoutScript: LomoutScript
    private lateinit var entityType: KClass<out Document>
    private lateinit var repository: EntityRepositoryInterface
    private lateinit var timestampOne: LocalDateTime

    @BeforeAll
    internal fun initAll() {
        System.setSecurityManager(NoExitSecurityManager())
        val util = LoadingDataTestPrepare()
        lomoutScript = util.loadConfiguration("${System.getenv("TEST_DATA_DIR")}/test.lomout.kts")
        repository = EntityRepository(lomoutScript.database, Level.ERROR)
        LomoutContext.setContext(createContext {
            script = lomoutScript
            scriptFile = File("${System.getenv("TEST_DATA_DIR")}/test.lomout.kts")
            this.logger = LogManager.getLogger(LOADER_LOG_NAME)
            this.repository = this@DataLoadingTest.repository
        })
        @Suppress("UNCHECKED_CAST")
        entityType = lomoutScript.findEntityType("Test_lomout${'$'}TestEntityAttributes")!!
        repository.getIDs(entityType, includeDeleted = true).forEach { repository.delete(entityType, it) }
        timestampOne = Documents.timestamp
        println("timestampOne: $timestampOne")
    }

    @AfterAll
    internal fun tearDownAll() {
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    @DisplayName("There is no loaded entity")
    internal fun thereIsNoAnyLoadedEntity() {
        assertThat(repository.get(entityType, includeDeleted = true).count()).isEqualTo(0)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Load entity first time")
    internal inner class FirstLoadEntityTest {
        lateinit var timestampTwo: LocalDateTime
        @BeforeAll
        internal fun initAll() {
            repository.close()
            runBlocking { delay(1000L) }
            repository = EntityRepository(lomoutScript.database, Level.ERROR)
            LomoutContext.setContext(createContext(LomoutContext.getContext()) {
                this.repository = this@DataLoadingTest.repository
            })
            timestampTwo = Documents.timestamp
            println("timestampTwo: $timestampTwo")

            val load = lomoutScript.loader?.loads?.find {
                it.entity.simpleName == "TestEntityAttributes" &&
                        it.sources.first().file.file.name.endsWith("test.attributes.xls") &&
                        it.sources.first().sheet.definition == "name:test-stock"
            }
            loadEntities(load!!)
        }

        @Test
        @DisplayName("Six entities should be loaded")
        internal fun numberOfEntitiesTest() {
            assertThat(repository.get(entityType, includeDeleted = true).count()).isEqualTo(6)
        }

        @Test
        @DisplayName("All entities should be in state CREATED/CREATED")
        internal fun createdCreatedStateTest() {
            repository.get(entityType, includeDeleted = true).forEach {
                println("entity.createTime: ${it.createTime}")
                assertThat(it.removed).isEqualTo(false)
                assertThat(it.createTime).isEqualTo(timestampTwo)
                assertThat(it.updateTime).isEqualTo(timestampTwo)
                assertThat(it.toucheTime).isEqualTo(timestampTwo)
                assertThat(it.removeTime).isNull()
            }
        }

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @Nested
        @DisplayName("Repeat first loading with one removed and one updated entities")
        internal inner class SecondLoadEntityTest {
            private lateinit var timestampThree: LocalDateTime
            @BeforeAll
            internal fun initAll() {
                repository.close()
                runBlocking { delay(1000L) }
                repository = EntityRepository(lomoutScript.database, Level.ERROR)
                LomoutContext.setContext(createContext(LomoutContext.getContext()) {
                    repository = this@DataLoadingTest.repository
                })
                timestampThree = Documents.timestamp
                println("timestampThree: $timestampThree")

                val load = lomoutScript.loader?.loads?.find {
                    it.entity.simpleName == "TestEntityAttributes" &&
                            it.sources.first().file.file.name.endsWith("test.attributes.xls") &&
                            it.sources.first().sheet.definition == "name:test-stock"
                }
                val workbook = getHSSFWorkbook(load!!)
                val sheet = getHSSFSheet(workbook, load)
                sheet.removeRow(sheet.getRow(5))
                sheet.getRow(4).getCell(3).setCellValue(12.0)
                Configurator.setLevel(EXPOSED_LOG_NAME, Level.TRACE)
                loadEntities(load, workbook)
                Configurator.setLevel(EXPOSED_LOG_NAME, Level.ERROR)
            }

            @Test
            @DisplayName("Six entities should be loaded")
            internal fun numberOfEntitiesTes() {
                assertThat(repository.get(entityType, includeDeleted = true).count()).isEqualTo(6)
            }

            @Test
            @DisplayName("Four entities should be in state CREATED/UNCHANGED")
            internal fun createdCreatedStateTest() {
                assertThat(repository.get(entityType, includeDeleted = true).filter {
                    !it.removed &&
                            it.createTime == timestampTwo &&
                            it.updateTime == timestampTwo &&
                            it.toucheTime == timestampThree &&
                            it.removeTime == null
                }.count()).isEqualTo(4)
            }

            @Test
            @DisplayName("One entity should be in state CREATED/UPDATED")
            internal fun createdUpdatedStateTest() {
                assertThat(repository.get(entityType, includeDeleted = true).filter {
                    !it.removed &&
                            it.createTime == timestampTwo &&
                            it.updateTime == timestampThree &&
                            it.toucheTime == timestampThree &&
                            it.removeTime == null
                }.count()).isEqualTo(1)
            }

            @Test
            @DisplayName("One entity should be in state CREATED/REMOVED")
            internal fun createdRemovedStateTest() {
                assertThat(repository.get(entityType, includeDeleted = true).filter {
                    it.removed && it.removeTime == timestampThree
                }.count()).isEqualTo(1)
            }

            @Nested
            @TestInstance(TestInstance.Lifecycle.PER_CLASS)
            @DisplayName("Repeat first loading with one deleted entity")
            internal inner class ThirdLoadingTest {
                private lateinit var timestampFour: LocalDateTime

                @BeforeAll
                internal fun initAll() {
                    repository.close()
                    runBlocking { delay(1000L) }
                    repository = EntityRepository(lomoutScript.database, Level.ERROR)
                    LomoutContext.setContext(
                        createContext(
                            LomoutContext.getContext()
                        ) {
                            repository = this@DataLoadingTest.repository
                        })
                    timestampFour = Documents.timestamp
                    println("timestampFour: $timestampFour")

                    val load = lomoutScript.loader?.loads?.find {
                        it.entity.simpleName == "TestEntityAttributes" &&
                                it.sources.first().file.file.name.endsWith("test.attributes.xls") &&
                                it.sources.first().sheet.definition == "name:test-stock"
                    }
                    val workbook = getHSSFWorkbook(load!!)
                    val sheet = getHSSFSheet(workbook, load)
                    sheet.removeRow(sheet.getRow(5))
                    val skuAttr = entityType.documentMetadata.attributes.getValue("sku")
                    val entity = repository.get(entityType, mapOf(skuAttr to "2"), includeDeleted = true)
                    @Suppress("MagicNumber")
                    entity!!.removeTime = LocalDateTime.now().minusDays(11)
                    repository.update(entity)
                    loadEntities(load, workbook)
                }

                @Test
                @DisplayName("Five entities should be loaded")
                internal fun numberOfEntitiesTest() {
                    assertThat(repository.get(entityType, includeDeleted = true).count()).isEqualTo(5)
                }

                @Nested
                @TestInstance(TestInstance.Lifecycle.PER_CLASS)
                @DisplayName("Update entity attribute with null value (LM-54)")
                internal inner class UpdateWithNullTest {
                    private lateinit var timestampFive: LocalDateTime

                    @BeforeAll
                    internal fun initAll() {
                        repository.close()
                        runBlocking { delay(1000L) }
                        repository = EntityRepository(lomoutScript.database, Level.ERROR)
                        LomoutContext.setContext(
                            createContext(
                                LomoutContext.getContext()
                            ) {
                                this.repository = this@DataLoadingTest.repository
                            })
                        timestampFive = Documents.timestamp
                        println("timestampFive: $timestampFive")

                        val load = lomoutScript.loader?.loads?.find {
                            it.entity.simpleName == "TestEntityAttributes" &&
                                    it.sources.first().file.file.name.endsWith("test.attributes.xls") &&
                                    it.sources.first().sheet.definition == "name:test-stock"
                        }
                        val workbook = getHSSFWorkbook(load!!)
                        val sheet = getHSSFSheet(workbook, load)
                        sheet.getRow(4).getCell(4).setBlank()
                        loadEntities(load, workbook)
                    }

                    @Test
                    @DisplayName("double_val attribute should be null")
                    internal fun numberOfEntitiesTest() {
                        val skuAttr = entityType.documentMetadata.attributes.getValue("sku")
                        val entity = repository.get(entityType, mapOf(skuAttr to "1"), includeDeleted = true)
                        @Suppress("UsePropertyAccessSyntax")
                        assertThat(entity).isNotNull()
                        assertThat(entity?.getAttribute("double_val")).isNull()
                    }
                }
            }
        }
    }

    private fun loadEntities(load: Load<*>, hssfWorkbook: HSSFWorkbook? = null) {
        val sheetDef = load.sources.first().sheet
        val excelWorkbook = hssfWorkbook ?: getHSSFWorkbook(load)
        excelWorkbook.use { workbook ->
            val excelSheet = workbook.sheetIterator().asSequence().find { sheetDef.isMatch(it.sheetName) }!!
            val loader = EntityLoader(
                load,
                EmptyRowBehavior.STOP,
                ExcelWorkbook(excelWorkbook)[excelSheet.sheetName]
            )
            loader.load()
        }
    }

    private fun getHSSFWorkbook(load: Load<*>): HSSFWorkbook {
        val file = load.sources.first().file.file
        return HSSFWorkbook(file.inputStream())
    }

    private fun getHSSFSheet(workbook: HSSFWorkbook, load: Load<*>): Sheet {
        val sheetDef = load.sources.first().sheet
        return workbook.sheetIterator().asSequence().find { sheetDef.isMatch(it.sheetName) }!!
    }
}
