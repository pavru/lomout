package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeCollection
import net.pototskiy.apps.magemediation.api.entity.AttributeReaderWithPlugin
import net.pototskiy.apps.magemediation.api.entity.BooleanListType
import net.pototskiy.apps.magemediation.api.entity.BooleanType
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.excel.ExcelWorkbook
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
import java.text.ParseException
import kotlin.reflect.full.createInstance

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DefaultBooleanReaderTest {

    private val typeManager = EntityTypeManager()
    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<BooleanType>
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell

    @BeforeEach
    internal fun setUp() {
        attr = typeManager.createAttribute("attr", BooleanType::class)
        entity = typeManager.createEntityType("test", emptyList(), false).also {
            typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr)))
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
    internal fun readDoubleCellTest() {
        val reader = BooleanAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(1.0)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(reader.read(attr, inputCell)?.value).isTrue()
        xlsTestDataCell.setCellValue(0.0)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(reader.read(attr, inputCell)?.value).isFalse()
    }

    @Test
    internal fun readBooleanCellTest() {
        val reader = BooleanAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(true)
        assertThat(inputCell.cellType).isEqualTo(CellType.BOOL)
        assertThat(reader.read(attr, inputCell)?.value).isTrue()
        xlsTestDataCell.setCellValue(false)
        assertThat(inputCell.cellType).isEqualTo(CellType.BOOL)
        assertThat(reader.read(attr, inputCell)?.value).isFalse()
    }

    @Test
    internal fun readStringEnUsCorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue("true")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isTrue()
        xlsTestDataCell.setCellValue("false")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isFalse()
    }

    @Test
    internal fun readStringEnUsIncorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue("not boolean")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }.isInstanceOf(ParseException::class.java)
    }

    @Test
    internal fun readStringRuRuCorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue("истИНа")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isTrue()
        xlsTestDataCell.setCellValue("Ложь")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isFalse()
    }

    @Test
    internal fun readStringRuRuIncorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue("какая-то строка")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }.isInstanceOf(ParseException::class.java)
    }

    @Test
    internal fun defaultBooleanReaderTest() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[BooleanType::class]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReaderWithPlugin::class.java)
        reader as AttributeReaderWithPlugin
        assertThat(reader.pluginClass).isEqualTo(BooleanAttributeReader::class)
        val v = reader.pluginClass.createInstance() as BooleanAttributeReader
        @Suppress("UNCHECKED_CAST")
        v.apply(reader.options as (BooleanAttributeReader.() -> Unit))
        assertThat(v.locale).isEqualTo(DEFAULT_LOCALE_STR)
    }

    @Test
    internal fun defaultBooleanListReader() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[BooleanListType::class]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReaderWithPlugin::class.java)
        reader as AttributeReaderWithPlugin
        assertThat(reader.pluginClass).isEqualTo(BooleanListAttributeReader::class)
        val v = reader.pluginClass.createInstance() as BooleanListAttributeReader
        @Suppress("UNCHECKED_CAST")
        v.apply(reader.options as (BooleanListAttributeReader.() -> Unit))
        assertThat(v.locale).isEqualTo(DEFAULT_LOCALE_STR)
        assertThat(v.delimiter).isEqualTo(',')
        assertThat(v.quote).isNull()
    }
}
