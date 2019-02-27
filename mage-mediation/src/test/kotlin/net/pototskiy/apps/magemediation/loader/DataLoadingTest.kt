package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.EmptyRowStrategy
import net.pototskiy.apps.magemediation.api.config.loader.Load
import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import net.pototskiy.apps.magemediation.api.database.EntityStatus
import net.pototskiy.apps.magemediation.api.entity.*
import net.pototskiy.apps.magemediation.api.source.workbook.excel.ExcelWorkbook
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
    private lateinit var eType: EType

    @BeforeAll
    fun initAll() {
        System.setSecurityManager(NoExitSecurityManager())
        EntityTypeManager.cleanEntityTypeConfiguration()
        Config.Builder.initConfigBuilder()
        // TODO: 23.02.2019 remove after test
        //EntityClass.initEntityCLassRegistrar()
        val util = LoadingDataTestPrepare()
        config = util.loadConfiguration("${System.getenv("TEST_DATA_DIR")}/test.conf.kts")
        util.initDataBase()
        eType = EntityTypeManager.getEntityType("onec-product")!!
        transaction { DbEntityTable.deleteAll() }
    }

    @Test
    @DisplayName("There is no any loaded entity")
    fun thereIsNoAnyLoadedEntity() {
        assertThat(DbEntity.getEntities(eType).count()).isZero()
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
            assertThat(DbEntity.getEntities(eType).count()).isEqualTo(6)
        }

        @Test
        @DisplayName("All entities should be in state CREATED/CREATED")
        fun createdCreatedStateTest() {
            DbEntity.getEntities(eType).forEach {
                assertThat(it.previousStatus).isEqualTo(EntityStatus.CREATED)
                assertThat(it.currentStatus).isEqualTo(EntityStatus.CREATED)
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
                assertThat(DbEntity.getEntities(eType).count()).isEqualTo(6)
            }

            @Test
            @DisplayName("Four entities should be in state CREATED/UNCHANGED")
            fun createdCreatedStateTest() {
                assertThat(DbEntity.getEntities(eType).filter {
                    it.previousStatus == EntityStatus.CREATED
                            && it.currentStatus == EntityStatus.UNCHANGED
                }.count()).isEqualTo(4)
            }

            @Test
            @DisplayName("One entities should be in state CREATED/UPDATED")
            fun createdUpdatedStateTest() {
                assertThat(DbEntity.getEntities(eType).filter {
                    it.previousStatus == EntityStatus.CREATED
                            && it.currentStatus == EntityStatus.UPDATED
                }.count()).isEqualTo(1)
            }

            @Test
            @DisplayName("One entity should be in state CREATED/REMOVED")
            fun createdRemovedStateTest() {
                assertThat(DbEntity.getEntities(eType).filter {
                    it.previousStatus == EntityStatus.CREATED
                            && it.currentStatus == EntityStatus.REMOVED
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
                    val skuAttr = EntityAttributeManager.getAttributeOrNull(AttributeName( eType.type, "sku"))!!
                    val entity = DbEntity.getEntityByKeys(eType,mapOf(skuAttr to StringValue("2")))!!
                    transaction { entity.removed = DateTime().minusDays(11) }
                    loadEntities(load, workbook)
                }

                @Test
                @DisplayName("Five entities should be loaded")
                fun numberOfEntitiesTest() {
                    assertThat(DbEntity.getEntities(eType).count()).isEqualTo(5)
                }
            }
        }
    }

    private fun loadEntities(load: Load, hssfWorkbook: HSSFWorkbook? = null) {
        val sheetDef = load.sources.first().sheet
        val excelWorkbook = hssfWorkbook ?: getHSSFWorkbook(load)
        excelWorkbook.use { workbook ->
            val excelSheet = workbook.sheetIterator().asSequence().find { sheetDef.isMatch(it.sheetName) }!!
            val loader = EntityLoader(
                load, EmptyRowStrategy.STOP, ExcelWorkbook(
                    excelWorkbook
                )[excelSheet.sheetName]
            )
            loader.load()
        }
        eType = EntityTypeManager.getEntityType("onec-product")!!
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
