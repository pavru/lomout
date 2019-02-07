package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.EmptyRowStrategy
import net.pototskiy.apps.magemediation.api.config.loader.Load
import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.database.EntityClass
import net.pototskiy.apps.magemediation.database.SourceEntities
import net.pototskiy.apps.magemediation.source.WorkbookFactory
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
    private lateinit var skuAttr: Attribute
    private lateinit var codeAttr: Attribute
    private lateinit var nameAttr: Attribute
    private val loads = mutableMapOf<String, Load>()
    private lateinit var entityClass: EntityClass<*>

    @BeforeAll
    fun initAll() {
        System.setSecurityManager(NoExitSecurityManager())
        Config.Builder.initConfigBuilder()
        EntityClass.initEntityCLassRegistrar()
        val util = LoadingDataTestPrepare()
        config = util.loadConfiguration("${System.getenv("TEST_DATA_DIR")}/test.conf.kts")
        util.initDataBase()
        skuAttr = config.loader.loads.find { it.entity.name == entityName }!!
            .entity.attributes.find { it.name == "sku" }!!
        codeAttr = config.loader.loads.find { it.entity.name == entityName }!!
            .entity.attributes.find { it.name == "group_code" }!!
        nameAttr = config.loader.loads.find { it.entity.name == entityName }!!
            .entity.attributes.find { it.name == "group_name" }!!
        loads[xlsLoad] = config.loader.loads.find {
            it.entity.name == entityName
                    && it.sources.first().file.file.name == "test.attributes.xls"
        }!!
        loads[csvLoad] = config.loader.loads.find {
            it.entity.name == entityName
                    && it.sources.first().file.file.name == "test.attributes.csv"
        }!!
    }


    @BeforeEach
    fun initEach() {
        transaction { SourceEntities.deleteAll() }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Six entities should be loaded")
    fun numberOfLoadedEntitiesTest(loadsID: String) {
        loadEntities(loadsID)
        assertThat(entityClass.getEntities().count()).isEqualTo(6)
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entities should have right group_code and group_name")
    fun groupCodeAndNameTest(loadID: String) {
        loadEntities(loadID)
        entityClass.getEntitiesWithAttributes().forEachIndexed { index, entity ->
            assertThat(entity.data[codeAttr] as String).isEqualTo("G00${index / 3 + 1}")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entities should have description = `description` + sku")
    fun entityDescriptionTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr(loadID, "description")
        entityClass.getEntitiesWithAttributes().forEachIndexed { _, entity ->
            assertThat(entity.data[attr] as String)
                .isEqualTo("description${entity.data[skuAttr]}")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity bool_val attributes should have right value")
    fun entityBoolValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr(loadID, "bool_val")
        entityClass.getEntitiesWithAttributes().forEachIndexed { index, entity ->
            assertThat(entity.data[attr] as Boolean).isEqualTo(index / 3 == 0)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity date_val attributes should have right value")
    fun entityDateValTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr(loadID, "date_val")
        entityClass.getEntitiesWithAttributes().forEachIndexed { i, entity ->
            assertThat(entity.data[attr] as DateTime).isEqualTo(
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
        val attr = attr(loadID, "datetime_val")
        entityClass.getEntitiesWithAttributes().forEachIndexed { i, entity ->
            (entity.data[attr] as DateTime).isEqual(
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
        val attr = attr(loadID, "string_list")
        entityClass.getEntitiesWithAttributes().forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr] as List<String>).containsExactlyElementsOf((i + 1..i + 3).map { "val$it" })
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity bool_list attributes should have right value")
    fun entityBoolListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr(loadID, "bool_list")
        entityClass.getEntitiesWithAttributes().forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr] as List<Boolean>)
                .containsExactlyElementsOf((0..2).toList().map { (i and (4 shr it)) != 0 })
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity long_list attributes should have right value")
    fun entityLongListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr(loadID, "long_list")
        entityClass.getEntitiesWithAttributes().forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr] as List<Long>)
                .containsExactlyElementsOf((10..12).toList().map { (it + i + 1).toLong() })
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity double_list attributes should have right value")
    fun entityDoubleListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr(loadID, "double_list")
        entityClass.getEntitiesWithAttributes().forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr] as List<Double>)
                .containsExactlyElementsOf(
                    (10..12).map { (it + i + 1).toDouble() + ((it + i + 1).toDouble() / 100.0) }
                )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity date_list attributes should have right value")
    fun entityDateListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr(loadID, "date_list")
        entityClass.getEntitiesWithAttributes().forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr] as List<DateTime>)
                .containsExactlyElementsOf(
                    (i + 7..i + 8)
                        .mapIndexed { j, v ->
                            DateTimeFormat.forPattern("d.M.yy")
                                .parseDateTime("$v.${j % 2 + 11}.${j % 2 + 11}")
                        }
                )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity datetime_list attributes should have right value")
    fun entityDateTimeListTest(loadID: String) {
        loadEntities(loadID)
        val attr = attr(loadID, "datetime_list")
        entityClass.getEntitiesWithAttributes().forEachIndexed { i, entity ->
            @Suppress("UNCHECKED_CAST")
            assertThat(entity.data[attr] as List<DateTime>)
                .containsExactlyElementsOf(
                    (i + 7..i + 8)
                        .mapIndexed { j, v ->
                            DateTimeFormat.forPattern("d.M.yy H:m")
                                .parseDateTime("$v.${j % 2 + 11}.${j % 2 + 11} $v:${j % 2 + 11}")
                        }
                )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity nested1 attributes should have right value")
    fun entityNested1Test(loadID: String) {
        loadEntities(loadID)
        val attr = attr(loadID, "nested1")
        entityClass.getEntitiesWithAttributes().forEachIndexed { i, entity ->
            assertThat(entity.data[attr]).isEqualTo((i + 11).toLong())
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [xlsLoad, csvLoad])
    @DisplayName("Entity nested1 attributes should have right value")
    fun entityNested2Test(loadID: String) {
        loadEntities(loadID)
        val attr = attr(loadID, "nested2")
        entityClass.getEntitiesWithAttributes().forEachIndexed { i, entity ->
            assertThat(entity.data[attr]).isEqualTo((i + 12).toLong())
        }
    }

    private fun attr(loadID: String, attrName: String) =
        loads[loadID]!!.entity.attributes.find { it.name == attrName }!!

    private fun loadEntities(loadID: String) {
        val load = loads[loadID]!!
        val file = load.sources.first().file.file
        val sheetDef = load.sources.first().sheet
        val workbook = WorkbookFactory.create(file.toURI().toURL())
        val sheet = workbook.find { sheetDef.isMatch(it.name) }!!
        val loader = EntityLoader(load, EmptyRowStrategy.STOP, sheet)
        loader.load()
        entityClass = EntityClass.getClass(entityName)!!
    }

    companion object {
        private const val xlsLoad = "xls"
        private const val csvLoad = "csv"
        private const val entityName = "onec-product"
    }
}
