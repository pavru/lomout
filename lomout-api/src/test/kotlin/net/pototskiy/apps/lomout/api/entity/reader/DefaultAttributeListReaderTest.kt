package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.AttributeReader
import net.pototskiy.apps.lomout.api.entity.AttributeReaderWithPlugin
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.type.ATTRIBUTELIST
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.reflect.full.createInstance

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DefaultAttributeListReaderTest {
    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<ATTRIBUTELIST>
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell
    private val entityTypeManager = EntityTypeManagerImpl()

    @BeforeEach
    internal fun setUp() {
        @Suppress("UNCHECKED_CAST")
        attr = entityTypeManager.createAttribute(
            "attr", ATTRIBUTELIST::class,
            builder = null,
            reader = defaultReaders[ATTRIBUTELIST::class] as AttributeReader<out ATTRIBUTELIST>,
            writer = defaultWriters[ATTRIBUTELIST::class] as AttributeWriter<out ATTRIBUTELIST>
        )
        entity = entityTypeManager.createEntityType("test", emptyList(), false).also {
            entityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr)))
        }
        xlsWorkbook = HSSFWorkbookFactory.createWorkbook()
        val xlsSheet = xlsWorkbook.createSheet("test-data")
        xlsSheet.isActive = true
        xlsTestDataCell = xlsSheet.createRow(0).createCell(0)
        workbook = ExcelWorkbook(xlsWorkbook)
        inputCell = workbook["test-data"][0]!![0]!!
    }

    @AfterEach
    internal fun tearDown() {
        workbook.close()
    }

    @Test
    internal fun readeAttributeListTest() {
        val reader = AttributeListReader().apply {
            delimiter = ','
            quote = null
            valueDelimiter = '='
            valueQuote = '\''
        }
        xlsTestDataCell.setCellValue("attr1='value1',attr2='value2'")
        val list = reader.read(attr, inputCell)?.value
        assertThat(list).isNotNull
        assertThat(list?.keys)
            .hasSize(2).containsExactlyElementsOf(listOf("attr1", "attr2"))
        assertThat(list?.values?.map { it.asString() })
            .hasSize(2).containsExactlyElementsOf(listOf("value1", "value2"))
    }

    @Test
    internal fun readAttributeListDoubleCellTest() {
        val reader = AttributeListReader().apply {
            delimiter = ','
            quote = null
            valueDelimiter = '='
            valueQuote = '\''
        }
        xlsTestDataCell.setCellValue(1.1)
        assertThatThrownBy { reader.read(attr, inputCell) }.isInstanceOf(AppDataException::class.java)
    }

    @Test
    internal fun readeAttributeListUnsuccessfulTest() {
        val reader = AttributeListReader().apply {
            delimiter = ','
            quote = null
            valueDelimiter = '='
            valueQuote = '\''
        }
        xlsTestDataCell.setCellValue("")
        assertThat(reader.read(attr, inputCell)).isNull()
        xlsTestDataCell.setCellValue("attr1,attr2")
        assertThat(reader.read(attr, inputCell)).hasSize(2)
        assertThat(reader.read(attr, inputCell)!!.map { (key, value) ->
            key to value.stringValue
        }.toMap()).containsAllEntriesOf(mapOf("attr1" to "", "attr2" to ""))
    }

    @Test
    internal fun defaultDateReader() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[ATTRIBUTELIST::class]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReaderWithPlugin::class.java)
        reader as AttributeReaderWithPlugin
        assertThat(reader.pluginClass).isEqualTo(AttributeListReader::class)
        val v = reader.pluginClass.createInstance() as AttributeListReader
        @Suppress("UNCHECKED_CAST")
        v.apply(reader.options as (AttributeListReader.() -> Unit))
        assertThat(v.quote).isNull()
        assertThat(v.delimiter).isEqualTo(',')
        assertThat(v.valueDelimiter).isEqualTo('=')
        assertThat(v.valueQuote).isEqualTo('"')
    }
}
