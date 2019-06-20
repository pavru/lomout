package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.EmptyRowBehavior
import net.pototskiy.apps.lomout.api.config.loader.Load
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.get
import net.pototskiy.apps.lomout.api.entity.type.BOOLEAN
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.entity.type.DOUBLE
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
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
import kotlin.collections.set


@Suppress("TooManyFunctions", "MagicNumber")
@DisplayName("Loading entity with all types attribute")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
@Execution(ExecutionMode.SAME_THREAD)
internal class LoaderAttributeLoadingTest {

    private lateinit var config: Config
    private lateinit var skuAttr: Attribute<STRING>
    private lateinit var codeAttr: Attribute<LONG>
    private lateinit var nameAttr: Attribute<STRING>
    private val loads = mutableMapOf<String, Load>()
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
        entityType = typeManager["onec-product"]
        repository.getIDs(entityType).forEach { repository.delete(it) }
        @Suppress("UNCHECKED_CAST")
        skuAttr = typeManager
            .getEntityAttribute(entityType, "sku") as Attribute<STRING>
        @Suppress("UNCHECKED_CAST")
        codeAttr = typeManager
            .getEntityAttribute(entityType, "group_code") as Attribute<LONG>
        @Suppress("UNCHECKED_CAST")
        nameAttr = typeManager
            .getEntityAttribute(entityType, "group_name") as Attribute<STRING>
        loads[xlsLoad] = config.loader?.loads?.find {
            it.entity.name == entityTypeName && it.sources.first().file.file.name == "test.attributes.xls"
        }!!
        loads[csvLoad] = config.loader?.loads?.find {
            it.entity.name == entityTypeName && it.sources.first().file.file.name == "test.attributes.csv"
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
        repository.getIDs(entityType).forEach { repository.delete(it) }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Six entities should be loaded")
    internal fun numberOfLoadedEntitiesTest(loadsID: String) {
        loadEntities(loadsID)
        assertThat(repository.get(entityType).count()).isEqualTo(6)
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entities should have right group_code and group_name")
    internal fun groupCodeAndNameTest(loadID: String) {
        loadEntities(loadID)
        repository.get(entityType).forEachIndexed { index, entity ->
            assertThat(entity.data[codeAttr]?.value as String).isEqualTo("G00${index / 3 + 1}")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entities should have description = `description` + sku")
    internal fun entityDescriptionTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("description")
        repository.get(entityType).forEachIndexed { _, entity ->
            assertThat(entity.data[attr]?.value as String)
                .isEqualTo("description${entity.data[skuAttr]}")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity bool_val attributes should have right value")
    internal fun entityBoolValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("bool_val")
        repository.get(entityType).forEach { entity ->
            val sku = (entity.data[skuAttr]?.value as? String)?.toShort()
            @Suppress("UsePropertyAccessSyntax")
            assertThat(sku).isNotNull()
            val expected = sku!! < 4
            assertThat(entity.data[attr]?.value as Boolean).isEqualTo(expected)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity date_val attributes should have right value")
    internal fun entityDateValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("date_val")
        repository.get(entityType).forEachIndexed { i, entity ->
            assertThat(entity.data[attr]?.value as DateTime).isEqualTo(
                DateTimeFormat
                    .forPattern("d.M.yy")
                    .parseDateTime("${i + 7}.${i + 7}.${i + 2007}")
            )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity datetime_val attributes should have right value")
    internal fun entityDateTimeValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("datetime_val")
        repository.get(entityType).forEachIndexed { i, entity ->
            (entity.data[attr]?.value as DateTime).isEqual(
                DateTimeFormat.forPattern("d.M.yy H:m")
                    .parseDateTime("${i + 7}.${i + 7}.${i + 2007} ${i + 7}:${i + 7}")
            )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity string_list attributes should have right value")
    internal fun entityStringListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("string_list")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<STRING>)
                .containsExactlyElementsOf((i + 1..i + 3).map { STRING("val$it") })
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity bool_list attributes should have right value")
    internal fun entityBoolListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("bool_list")
        repository.get(entityType).forEach { entity ->
            val sku = (entity.data[skuAttr] as? STRING)?.value?.toInt()
            @Suppress("UsePropertyAccessSyntax")
            assertThat(sku).isNotNull()
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<BOOLEAN>)
                .containsExactlyElementsOf((0..2).toList().map { BOOLEAN(((sku!! - 1) and (4 shr it)) != 0) })
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity long_list attributes should have right value")
    internal fun entityLongListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("long_list")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<LONG>)
                .containsExactlyElementsOf((10..12).toList().map { LONG((it + i + 1).toLong()) })
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity double_list attributes should have right value")
    internal fun entityDoubleListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("double_list")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<DOUBLE>)
                .containsExactlyElementsOf(
                    (10..12).map { DOUBLE((it + i + 1).toDouble() + ((it + i + 1).toDouble() / 100.0)) }
                )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity date_list attributes should have right value")
    internal fun entityDateListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("date_list")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<DATE>)
                .containsExactlyElementsOf(
                    (i + 7..i + 8)
                        .mapIndexed { j, v ->
                            DATE(
                                DateTimeFormat.forPattern("d.M.yy")
                                    .parseDateTime("$v.${j % 2 + 11}.${j % 2 + 11}")
                            )
                        }
                )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity datetime_list attributes should have right value")
    internal fun entityDateTimeListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("datetime_list")
        repository.get(entityType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<DATETIME>)
                .containsExactlyElementsOf(
                    (i + 7..i + 8)
                        .mapIndexed { j, v ->
                            DATETIME(
                                DateTimeFormat.forPattern("d.M.yy H:m")
                                    .parseDateTime("$v.${j % 2 + 11}.${j % 2 + 11} $v:${j % 2 + 11}")
                            )
                        }
                )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity nested1 attributes should have right value")
    internal fun entityNested1Test(loadID: String) {
        loadEntities(loadID)
        val attr = attr("nested1")
        repository.get(entityType).forEachIndexed { i, entity ->
            assertThat(entity.data[attr]?.value as Long).isEqualTo((i + 11).toLong())
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity nested1 attributes should have right value")
    internal fun entityNested2Test(loadID: String) {
        loadEntities(loadID)
        val attr = attr("nested2")
        repository.get(entityType).forEachIndexed { i, entity ->
            assertThat(entity.data[attr]?.value as Long).isEqualTo((i + 12).toLong())
        }
    }

    private fun attr(attrName: String) =
        typeManager.getEntityAttribute(entityType, attrName)!!

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
        entityType = typeManager.getEntityType(entityTypeName)!!
    }

    companion object {
        private const val xlsLoad = "xls"
        private const val csvLoad = "csv"
        private const val entityTypeName = "onec-product"
    }
}
