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

import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.document.documentMetadata
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.callable.CallableContext
import net.pototskiy.apps.lomout.api.script.EmptyRowBehavior
import net.pototskiy.apps.lomout.api.script.LomoutScript
import net.pototskiy.apps.lomout.api.script.loader.Load
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.set
import kotlin.reflect.KClass

@Suppress("TooManyFunctions", "MagicNumber")
@DisplayName("Loading entity with all types attribute")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
@Execution(ExecutionMode.SAME_THREAD)
internal class LoaderAttributeLoadingTest {

    private lateinit var lomoutScript: LomoutScript
    private lateinit var skuAttr: Attribute
    private lateinit var codeAttr: Attribute
    private lateinit var nameAttr: Attribute
    private val loads = mutableMapOf<String, Load<*>>()
    private lateinit var entityType: KClass<out Document>
    private lateinit var repository: EntityRepositoryInterface

    @BeforeAll
    internal fun initAll() {
        System.setSecurityManager(NoExitSecurityManager())
        val util = LoadingDataTestPrepare()
        lomoutScript = util.loadConfiguration("${System.getenv("TEST_DATA_DIR")}/test.lomout.kts")
        repository = EntityRepository(lomoutScript.database, Level.ERROR)
        CallableContext.lomoutScript = lomoutScript
        CallableContext.repository = repository
        @Suppress("UNCHECKED_CAST")
        entityType = lomoutScript.findEntityType("Test_lomout${'$'}TestEntityAttributes")!!
        repository.getIDs(entityType).forEach { repository.delete(entityType, it) }
        @Suppress("UNCHECKED_CAST")
        skuAttr = entityType.documentMetadata.attributes.getValue("sku")
        codeAttr = entityType.documentMetadata.attributes.getValue("group_code")
        nameAttr = entityType.documentMetadata.attributes.getValue("group_name")
        loads[xlsLoad] = lomoutScript.loader?.loads?.find {
            it.entity.simpleName == entityTypeName && it.sources.first().file.file.name == "test.attributes.xls"
        }!!
        loads[csvLoad] = lomoutScript.loader?.loads?.find {
            it.entity.simpleName == entityTypeName && it.sources.first().file.file.name == "test.attributes.csv"
        }!!

        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
//        Configurator.setLevel(EXPOSED_LOG_NAME, Level.DEBUG)
    }

    @AfterAll
    internal fun tearDownAll() {
        repository.close()
    }

    @BeforeEach
    internal fun initEach() {
        repository.getIDs(entityType).forEach { repository.delete(entityType, it) }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Six entities should be loaded")
    internal fun numberOfLoadedEntitiesTest(loadsID: String) {
        loadEntities(loadsID)
        assertThat(repository.get(entityType).count()).isEqualTo(6)
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entities should have right group_code and group_name")
    internal fun groupCodeAndNameTest(loadID: String) {
        loadEntities(loadID)
        repository.get(entityType).forEachIndexed { index, entity ->
            assertThat(entity.getAttribute(codeAttr.name) as String).isEqualTo("G00${index / 3 + 1}")
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entities should have description = `description` + sku")
    internal fun entityDescriptionTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("description")
        repository.get(entityType).forEachIndexed { _, entity ->
            assertThat(entity.getAttribute(attr.name) as String)
                .isEqualTo("description${entity.getAttribute(skuAttr.name)}")
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity bool_val attributes should have right value")
    internal fun entityBoolValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("bool_val")
        repository.get(entityType).forEach { entity ->
            val sku = (entity.getAttribute(skuAttr.name) as? String)?.toShort()
            @Suppress("UsePropertyAccessSyntax")
            assertThat(sku).isNotNull()
            val expected = sku!! < 4
            assertThat(entity.getAttribute(attr.name) as Boolean).isEqualTo(expected)
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity date_val attributes should have right value")
    internal fun entityDateValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("date_val")
        repository.get(entityType).forEachIndexed { i, entity ->
            assertThat(entity.getAttribute(attr.name) as LocalDate).isEqualTo(
                LocalDate.parse("${i + 7}.${i + 7}.${i + 2007}", DateTimeFormatter.ofPattern("d.M.uuuu"))
            )
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity datetime_val attributes should have right value")
    internal fun entityDateTimeValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("datetime_val")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("GraziInspection")
            (entity.getAttribute(attr.name) as LocalDateTime).isEqual(
                LocalDateTime.parse(
                    "${i + 7}.${i + 7}.${i + 2007} ${i + 7}:${i + 7}",
                    DateTimeFormatter.ofPattern("d.M.uuuu H:m")
                )
            )
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity string_list attributes should have right value")
    internal fun entityStringListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("string_list")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.getAttribute(attr.name) as List<String>)
                .containsExactlyElementsOf((i + 1..i + 3).map { "val$it" })
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity bool_list attributes should have right value")
    internal fun entityBoolListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("bool_list")
        repository.get(entityType).forEach { entity ->
            val sku = (entity.getAttribute(skuAttr.name) as? String)?.toInt()
            @Suppress("UsePropertyAccessSyntax")
            assertThat(sku).isNotNull()
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.getAttribute(attr.name) as List<Boolean>)
                .containsExactlyElementsOf((0..2).toList().map { (((sku!! - 1) and (4 shr it)) != 0) })
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity long_list attributes should have right value")
    internal fun entityLongListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("long_list")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.getAttribute(attr.name) as List<Long>)
                .containsExactlyElementsOf((10..12).toList().map { (it + i + 1).toLong() })
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity double_list attributes should have right value")
    internal fun entityDoubleListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("double_list")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.getAttribute(attr.name) as List<Double>)
                .containsExactlyElementsOf(
                    (10..12).map { ((it + i + 1).toDouble() + ((it + i + 1).toDouble() / 100.0)) }
                )
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity date_list attributes should have right value")
    internal fun entityDateListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("date_list")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.getAttribute(attr.name) as List<LocalDate>)
                .containsExactlyElementsOf(
                    (i + 7..i + 8)
                        .mapIndexed { j, v ->
                            LocalDate.parse(
                                "$v.${j % 2 + 11}.${j % 2 + 11}",
                                DateTimeFormatter.ofPattern("d.M.uu")
                            )
                        }
                )
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity datetime_list attributes should have right value")
    internal fun entityDateTimeListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("datetime_list")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.getAttribute(attr.name) as List<LocalDateTime>)
                .containsExactlyElementsOf(
                    (i + 7..i + 8)
                        .mapIndexed { j, v ->
                            LocalDateTime.parse(
                                "$v.${j % 2 + 11}.${j % 2 + 11} $v:${j % 2 + 11}",
                                DateTimeFormatter.ofPattern("d.M.uu H:m")
                            )
                        }
                )
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity nested1 attributes should have right value")
    internal fun entityNested1Test(loadID: String) {
        loadEntities(loadID)
        val attr = attr("compound")
        repository.get(entityType).forEachIndexed { i, entity ->
            assertThat((entity.getAttribute(attr.name) as Document).getAttribute("nested1") as Long)
                .isEqualTo((i + 11).toLong())
        }
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity nested1 attributes should have right value")
    internal fun entityNested2Test(loadID: String) {
        loadEntities(loadID)
        val attr = attr("compound")
        repository.get(entityType).forEachIndexed { i, entity ->
            assertThat((entity.getAttribute(attr.name) as Document).getAttribute("nested2") as Long)
                .isEqualTo((i + 12).toLong())
        }
    }

    private fun attr(attrName: String) = entityType.documentMetadata.attributes.getValue(attrName)

    private fun loadEntities(loadID: String) {
        val load = loads[loadID]!!
        val file = load.sources.first().file.file
        val locale = load.sources.first().file.locale
        val sheetDef = load.sources.first().sheet
        WorkbookFactory.create(file.toURI().toURL(), locale).use { workbook ->
            val sheet = workbook.find { sheetDef.isMatch(it.name) }!!
            val loader = EntityLoader(repository, load, EmptyRowBehavior.STOP, sheet)
            loader.load()
        }
//        @Suppress("UNCHECKED_CAST")
//        entityType = Class.forName("Test_conf.TestEntityAttributes").kotlin as KClass<out Document>
    }

    companion object {
        private const val xlsLoad = "xls"
        private const val csvLoad = "csv"
        private const val entityTypeName = "TestEntityAttributes"
    }
}
