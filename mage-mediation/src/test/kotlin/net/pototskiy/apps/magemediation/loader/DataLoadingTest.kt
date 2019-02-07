package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.EmptyRowStrategy
import net.pototskiy.apps.magemediation.api.config.loader.Load
import net.pototskiy.apps.magemediation.api.database.EntityClass
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.database.SourceDataStatus
import net.pototskiy.apps.magemediation.database.SourceEntities
import net.pototskiy.apps.magemediation.database.SourceEntity
import net.pototskiy.apps.magemediation.source.excel.ExcelWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Loading entity from source file")
class DataLoadingTest {
    private lateinit var config: Config
    private lateinit var entityClass: EntityClass<PersistentSourceEntity>

    @BeforeAll
    fun initAll() {
        System.setSecurityManager(NoExitSecurityManager())
        Config.Builder.initConfigBuilder()
        EntityClass.initEntityCLassRegistrar()
        val util = LoadingDataTestPrepare()
        config = util.loadConfiguration("${System.getenv("TEST_DATA_DIR")}/test.conf.kts")
        util.initDataBase()
        entityClass = EntityClass(
            "onec-product",
            SourceEntity,
            emptyList(),
            true
        )
        transaction { SourceEntities.deleteAll() }
    }

    @Test
    @DisplayName("There is no any loaded entity")
    fun thereIsNoAnyLoadedEntity() {
        assertThat(entityClass.getEntities().count()).isZero()
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Load entity first time")
    inner class FirstLoadEntityTest {

        @BeforeAll
        fun initAll() {
            val load = config.loader.loads.find {
                it.entity.name == "onec-product"
                        && it.sources.first().file.file.name.endsWith("test.attributes.xls")
                        && it.sources.first().sheet.definition == "name:test-stock"
            }
            loadEntities(load!!)
        }

        @Test
        @DisplayName("Six entities should be loaded")
        fun numberOfEntitiesTest() {
            assertThat(entityClass.getEntities().count()).isEqualTo(6)
        }

        @Test
        @DisplayName("All entities should be in state CREATED/CREATED")
        fun createdCreatedStateTest() {
            entityClass.getEntities().forEach {
                assertThat(it.previousStatus).isEqualTo(SourceDataStatus.CREATED)
                assertThat(it.currentStatus).isEqualTo(SourceDataStatus.CREATED)
            }
        }

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @Nested
        @DisplayName("Repeat first loading with one removed and one updated entities")
        inner class SecondLoadEntityTest {
            @BeforeAll
            fun initAll() {
                val load = config.loader.loads.find {
                    it.entity.name == "onec-product"
                            && it.sources.first().file.file.name.endsWith("test.attributes.xls")
                            && it.sources.first().sheet.definition == "name:test-stock"
                }
                val workbook = getHSSFWorkbook(load!!)
                val sheet = getHSSFSheet(workbook, load)
                sheet.removeRow(sheet.getRow(5))
                sheet.getRow(4).getCell(3).setCellValue(12.0)
                loadEntities(load, workbook)
            }

            @Test
            @DisplayName("Six entities should be loaded")
            fun numberOfEntitiesTes() {
                assertThat(entityClass.getEntities().count()).isEqualTo(6)
            }

            @Test
            @DisplayName("Five entities should be in state CREATED/UNCHANGED")
            fun createdCreatedStateTest() {
                assertThat(entityClass.getEntities().filter {
                    it.previousStatus == SourceDataStatus.CREATED
                            && it.currentStatus == SourceDataStatus.UNCHANGED
                }.count()).isEqualTo(4)
            }

            @Test
            @DisplayName("One entities should be in state CREATED/UPDATED")
            fun createdUpdatedStateTest() {
                assertThat(entityClass.getEntities().filter {
                    it.previousStatus == SourceDataStatus.UNCHANGED
                            && it.currentStatus == SourceDataStatus.UPDATED
                }.count()).isEqualTo(1)
            }

            @Test
            @DisplayName("One entity should be in state CREATED/REMOVED")
            fun createdRemovedStateTest() {
                assertThat(entityClass.getEntities().filter {
                    it.previousStatus == SourceDataStatus.CREATED
                            && it.currentStatus == SourceDataStatus.REMOVED
                }.count()).isEqualTo(1)
            }

            @Nested
            @TestInstance(TestInstance.Lifecycle.PER_CLASS)
            @DisplayName("Repeat first loading with one deleted entity")
            inner class ThirdLoadingTest {
                @BeforeAll
                fun initAll() {
                    val load = config.loader.loads.find {
                        it.entity.name == "onec-product"
                                && it.sources.first().file.file.name.endsWith("test.attributes.xls")
                                && it.sources.first().sheet.definition == "name:test-stock"
                    }
                    val workbook = getHSSFWorkbook(load!!)
                    val sheet = getHSSFSheet(workbook, load)
                    sheet.removeRow(sheet.getRow(5))
                    val skuAttr = entityClass.attributes.findLast { it.name == "sku" }!!
                    val entity = entityClass.getEntityByKeys(mapOf(skuAttr to "2")) as PersistentSourceEntity
                    transaction { entity.removedInMedium = DateTime().minusDays(11) }
                    loadEntities(load, workbook)
                }

                @Test
                @DisplayName("Five entities should be loaded")
                fun numberOfEntitiesTest() {
                    assertThat(entityClass.getEntities().count()).isEqualTo(5)
                }
            }
        }
    }

    private fun loadEntities(load: Load, hssfWorkbook: HSSFWorkbook? = null) {
        val sheetDef = load.sources.first().sheet
        val excelWorkbook = hssfWorkbook ?: getHSSFWorkbook(load)
        val excelSheet = excelWorkbook.sheetIterator().asSequence().find { sheetDef.isMatch(it.sheetName) }!!
        val loader = EntityLoader(load, EmptyRowStrategy.STOP, ExcelWorkbook(excelWorkbook)[excelSheet.sheetName])
        loader.load()
        @Suppress("UNCHECKED_CAST")
        entityClass = EntityClass.getClass("onec-product") as EntityClass<PersistentSourceEntity>
    }

    private fun getHSSFWorkbook(load: Load): HSSFWorkbook {
        val file = load.sources.first().file.file
        return HSSFWorkbook(file.inputStream())
    }

    private fun getHSSFSheet(workbook: HSSFWorkbook, load: Load): Sheet {
        val sheetDef = load.sources.first().sheet
        return workbook.sheetIterator().asSequence().find { sheetDef.isMatch(it.sheetName) }!!
    }

}
