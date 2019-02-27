package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.ROOT_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.EmptyRowStrategy
import net.pototskiy.apps.magemediation.api.config.loader.Load
import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import net.pototskiy.apps.magemediation.api.entity.*
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookFactory
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Loading entity with all types attribute")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoaderAttributeLoadingTest {

    private lateinit var config: Config
    private lateinit var skuAttr: Attribute<StringType>
    private lateinit var codeAttr: Attribute<LongType>
    private lateinit var nameAttr: Attribute<StringType>
    private val loads = mutableMapOf<String, Load>()
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
        @Suppress("UNCHECKED_CAST")
        skuAttr = EntityAttributeManager
            .getAttributeOrNull(AttributeName(entityTypeName, "sku")) as Attribute<StringType>
        @Suppress("UNCHECKED_CAST")
        codeAttr = EntityAttributeManager
            .getAttributeOrNull(AttributeName(entityTypeName, "group_code")) as Attribute<LongType>
        @Suppress("UNCHECKED_CAST")
        nameAttr = EntityAttributeManager
            .getAttributeOrNull(AttributeName(entityTypeName, "group_name")) as Attribute<StringType>
        loads[xlsLoad] = config.loader.loads.find {
            it.entity.name == entityTypeName
                    && it.sources.first().file.file.name == "test.attributes.xls"
        }!!
        loads[csvLoad] = config.loader.loads.find {
            it.entity.name == entityTypeName
                    && it.sources.first().file.file.name == "test.attributes.csv"
        }!!

        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
//        Configurator.setLevel(EXPOSED_LOG_NAME, Level.DEBUG)
    }


    @BeforeEach
    fun initEach() {
        transaction { DbEntityTable.deleteAll() }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Six entities should be loaded")
    fun numberOfLoadedEntitiesTest(loadsID: String) {
        loadEntities(loadsID)
        assertThat(DbEntity.getEntities(eType).count()).isEqualTo(6)
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entities should have right group_code and group_name")
    fun groupCodeAndNameTest(loadID: String) {
        loadEntities(loadID)
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { index, entity ->
            assertThat(entity.data[codeAttr]?.value as String).isEqualTo("G00${index / 3 + 1}")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entities should have description = `description` + sku")
    fun entityDescriptionTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("description")
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { _, entity ->
            assertThat(entity.data[attr]?.value as String)
                .isEqualTo("description${entity.data[skuAttr]}")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity bool_val attributes should have right value")
    fun entityBoolValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("bool_val")
        DbEntity.getEntitiesWithAttributes(eType).forEach { entity ->
            val sku = (entity.data[skuAttr]?.value as? String)?.toShort()
            assertThat(sku).isNotNull()
            val expected = sku!! < 4
            assertThat(entity.data[attr]?.value as Boolean).isEqualTo(expected)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity date_val attributes should have right value")
    fun entityDateValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("date_val")
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { i, entity ->
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
    fun entityDateTimeValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("datetime_val")
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { i, entity ->
            (entity.data[attr]?.value as DateTime).isEqual(
                DateTimeFormat.forPattern("d.M.yy H:m")
                    .parseDateTime("${i + 7}.${i + 7}.${i + 2007} ${i + 7}:${i + 7}")
            )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity string_list attributes should have right value")
    fun entityStringListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("string_list")
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<StringValue>)
                .containsExactlyElementsOf((i + 1..i + 3).map { StringValue("val$it") })
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity bool_list attributes should have right value")
    fun entityBoolListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("bool_list")
        DbEntity.getEntitiesWithAttributes(eType).forEach { entity ->
            val sku = (entity.data[skuAttr] as? StringType)?.value?.toInt()
            assertThat(sku).isNotNull()
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<BooleanType>)
                .containsExactlyElementsOf((0..2).toList().map { BooleanValue(((sku!!-1) and (4 shr it)) != 0) })
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity long_list attributes should have right value")
    fun entityLongListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("long_list")
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<LongType>)
                .containsExactlyElementsOf((10..12).toList().map { LongValue((it + i + 1).toLong()) })
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity double_list attributes should have right value")
    fun entityDoubleListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("double_list")
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<DoubleType>)
                .containsExactlyElementsOf(
                    (10..12).map { DoubleValue((it + i + 1).toDouble() + ((it + i + 1).toDouble() / 100.0)) }
                )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity date_list attributes should have right value")
    fun entityDateListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("date_list")
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<DateType>)
                .containsExactlyElementsOf(
                    (i + 7..i + 8)
                        .mapIndexed { j, v ->
                            DateValue(
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
    fun entityDateTimeListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr("datetime_list")
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr]?.value as List<DateTimeType>)
                .containsExactlyElementsOf(
                    (i + 7..i + 8)
                        .mapIndexed { j, v ->
                            DateTimeValue(
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
    fun entityNested1Test(loadID: String) {
        loadEntities(loadID)
        val attr = attr("nested1")
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { i, entity ->
            assertThat(entity.data[attr]?.value as Long).isEqualTo((i + 11).toLong())
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity nested1 attributes should have right value")
    fun entityNested2Test(loadID: String) {
        loadEntities(loadID)
        val attr = attr("nested2")
        DbEntity.getEntitiesWithAttributes(eType).forEachIndexed { i, entity ->
            assertThat(entity.data[attr]?.value as Long).isEqualTo((i + 12).toLong())
        }
    }

    private fun attr(attrName: String) =
        EntityAttributeManager.getAttributeOrNull(AttributeName(eType.type, attrName))!!

    private fun loadEntities(loadID: String) {
        val load = loads[loadID]!!
        val file = load.sources.first().file.file
        val locale = load.sources.first().file.locale
        val sheetDef = load.sources.first().sheet
        WorkbookFactory.create(file.toURI().toURL(), locale).use { workbook ->
            val sheet = workbook.find { sheetDef.isMatch(it.name) }!!
            val loader = EntityLoader(load, EmptyRowStrategy.STOP, sheet)
            loader.load()
        }
        eType = EntityTypeManager.getEntityType(entityTypeName)!!
    }

    companion object {
        private const val xlsLoad = "xls"
        private const val csvLoad = "csv"
        private const val entityTypeName = "onec-product"
    }
}
