package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.EmptyRowBehavior
import net.pototskiy.apps.lomout.api.config.loader.Load
import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.database.EntityStatus
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.StringType
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Loading entity from source file")
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
@Execution(ExecutionMode.SAME_THREAD)
internal class DataLoadingTest {
    private lateinit var config: Config
    private lateinit var entityType: EntityType
    private lateinit var typeManager: EntityTypeManager

    @BeforeAll
    internal fun initAll() {
        System.setSecurityManager(NoExitSecurityManager())
        val util = LoadingDataTestPrepare()
        config = util.loadConfiguration("${System.getenv("TEST_DATA_DIR")}/test.conf.kts")
        typeManager = config.entityTypeManager
        util.initDataBase(typeManager)
        PluginContext.config = config
        PluginContext.entityTypeManager = config.entityTypeManager
        entityType = typeManager.getEntityType("onec-product")!!
        transaction { DbEntityTable.deleteAll() }
    }

    @Test
    @DisplayName("There is no loaded entity")
    internal fun thereIsNoAnyLoadedEntity() {
        assertThat(DbEntity.getEntities(entityType).count()).isZero()
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Load entity first time")
    internal inner class FirstLoadEntityTest {

        @BeforeAll
        internal fun initAll() {
            val load = config.loader?.loads?.find {
                it.entity.name == "onec-product" &&
                        it.sources.first().file.file.name.endsWith("test.attributes.xls") &&
                        it.sources.first().sheet.definition == "name:test-stock"
            }
            loadEntities(load!!)
        }

        @Test
        @DisplayName("Six entities should be loaded")
        internal fun numberOfEntitiesTest() {
            assertThat(DbEntity.getEntities(entityType).count()).isEqualTo(6)
        }

        @Test
        @DisplayName("All entities should be in state CREATED/CREATED")
        internal fun createdCreatedStateTest() {
            DbEntity.getEntities(entityType).forEach {
                assertThat(it.previousStatus).isEqualTo(EntityStatus.CREATED)
                assertThat(it.currentStatus).isEqualTo(EntityStatus.CREATED)
            }
        }

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @Nested
        @DisplayName("Repeat first loading with one removed and one updated entities")
        internal inner class SecondLoadEntityTest {
            @BeforeAll
            internal fun initAll() {
                val load = config.loader?.loads?.find {
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
            internal fun numberOfEntitiesTes() {
                assertThat(DbEntity.getEntities(entityType).count()).isEqualTo(6)
            }

            @Test
            @DisplayName("Four entities should be in state CREATED/UNCHANGED")
            internal fun createdCreatedStateTest() {
                assertThat(DbEntity.getEntities(entityType).filter {
                    it.previousStatus == EntityStatus.CREATED
                            && it.currentStatus == EntityStatus.UNCHANGED
                }.count()).isEqualTo(4)
            }

            @Test
            @DisplayName("One entity should be in state CREATED/UPDATED")
            internal fun createdUpdatedStateTest() {
                assertThat(DbEntity.getEntities(entityType).filter {
                    it.previousStatus == EntityStatus.CREATED
                            && it.currentStatus == EntityStatus.UPDATED
                }.count()).isEqualTo(1)
            }

            @Test
            @DisplayName("One entity should be in state CREATED/REMOVED")
            internal fun createdRemovedStateTest() {
                assertThat(DbEntity.getEntities(entityType).filter {
                    it.previousStatus == EntityStatus.CREATED
                            && it.currentStatus == EntityStatus.REMOVED
                }.count()).isEqualTo(1)
            }

            @Nested
            @TestInstance(TestInstance.Lifecycle.PER_CLASS)
            @DisplayName("Repeat first loading with one deleted entity")
            internal inner class ThirdLoadingTest {
                @BeforeAll
                internal fun initAll() {
                    val load = config.loader?.loads?.find {
                        it.entity.name == "onec-product" &&
                                it.sources.first().file.file.name.endsWith("test.attributes.xls") &&
                                it.sources.first().sheet.definition == "name:test-stock"
                    }
                    val workbook = getHSSFWorkbook(load!!)
                    val sheet = getHSSFSheet(workbook, load)
                    sheet.removeRow(sheet.getRow(5))
                    val skuAttr = typeManager.getEntityAttribute(entityType, "sku")!!
                    val entity = DbEntity.getEntitiesByAttributes(
                        entityType,
                        mapOf(skuAttr to StringType("2")),
                        true
                    ).first()
                    @Suppress("MagicNumber")
                    transaction { entity.removed = DateTime().minusDays(11) }
                    loadEntities(load, workbook)
                }

                @Test
                @DisplayName("Five entities should be loaded")
                internal fun numberOfEntitiesTest() {
                    assertThat(DbEntity.getEntities(entityType).count()).isEqualTo(5)
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
                load, EmptyRowBehavior.STOP, ExcelWorkbook(
                    excelWorkbook
                )[excelSheet.sheetName]
            )
            loader.load()
        }
        entityType = typeManager.getEntityType("onec-product")!!
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
