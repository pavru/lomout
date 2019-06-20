package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.EmptyRowBehavior
import net.pototskiy.apps.lomout.api.config.loader.Load
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.entity.EntityStatus
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Loading entity from source file")
@Execution(ExecutionMode.SAME_THREAD)
internal class DataLoadingTest {
    private lateinit var config: Config
    private lateinit var entityType: EntityType
    private lateinit var typeManager: EntityTypeManagerImpl
    private lateinit var repository: EntityRepositoryInterface

    @BeforeAll
    internal fun initAll() {
        System.setSecurityManager(NoExitSecurityManager())
        val util = LoadingDataTestPrepare()
        config = util.loadConfiguration("${System.getenv("TEST_DATA_DIR")}/test.conf.kts")
        typeManager = config.entityTypeManager
        repository = EntityRepository(config.database, typeManager, Level.ERROR)
        PluginContext.config = config
        PluginContext.entityTypeManager = config.entityTypeManager
        entityType = typeManager.getEntityType("onec-product")!!
        repository.getIDs(entityType).forEach { repository.delete(it) }
    }

    @AfterAll
    internal fun tearDownAll() {
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    @DisplayName("There is no loaded entity")
    internal fun thereIsNoAnyLoadedEntity() {
        assertThat(repository.get(entityType).count()).isEqualTo(0)
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
            assertThat(repository.get(entityType).count()).isEqualTo(6)
        }

        @Test
        @DisplayName("All entities should be in state CREATED/CREATED")
        internal fun createdCreatedStateTest() {
            repository.get(entityType).forEach {
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
                    it.entity.name == "onec-product" &&
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
                assertThat(repository.get(entityType).count()).isEqualTo(6)
            }

            @Test
            @DisplayName("Four entities should be in state CREATED/UNCHANGED")
            internal fun createdCreatedStateTest() {
                assertThat(repository.get(entityType).filter {
                    it.previousStatus == EntityStatus.CREATED &&
                            it.currentStatus == EntityStatus.UNCHANGED
                }.count()).isEqualTo(4)
            }

            @Test
            @DisplayName("One entity should be in state CREATED/UPDATED")
            internal fun createdUpdatedStateTest() {
                assertThat(repository.get(entityType).filter {
                    it.previousStatus == EntityStatus.CREATED &&
                            it.currentStatus == EntityStatus.UPDATED
                }.count()).isEqualTo(1)
            }

            @Test
            @DisplayName("One entity should be in state CREATED/REMOVED")
            internal fun createdRemovedStateTest() {
                assertThat(repository.get(entityType).filter {
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
                    val entity = repository.get(
                        entityType,
                        mapOf(skuAttr to STRING("2"))
                    )
                    @Suppress("MagicNumber")
                    entity!!.removed = DateTime().minusDays(11)
                    repository.update(entity)
                    loadEntities(load, workbook)
                }

                @Test
                @DisplayName("Five entities should be loaded")
                internal fun numberOfEntitiesTest() {
                    assertThat(repository.get(entityType).count()).isEqualTo(5)
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
                repository,
                load,
                EmptyRowBehavior.STOP,
                ExcelWorkbook(excelWorkbook)[excelSheet.sheetName]
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
