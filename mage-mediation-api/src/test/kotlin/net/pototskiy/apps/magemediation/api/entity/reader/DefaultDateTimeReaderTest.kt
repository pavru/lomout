package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeCollection
import net.pototskiy.apps.magemediation.api.entity.AttributeReaderWithPlugin
import net.pototskiy.apps.magemediation.api.entity.DateTimeType
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.excel.ExcelWorkbook
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.reflect.full.createInstance

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DefaultDateTimeReaderTest {
    private val typeManager = EntityTypeManager()
    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<DateTimeType>
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell

    @BeforeEach
    internal fun setUp() {
        attr = typeManager.createAttribute("attr", DateTimeType::class)
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
        val expected = DateTime().withTime(7, 21, 0, 0)
        val readerEnUs = DateTimeAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateTimeAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue(HSSFDateUtil.getExcelDate(expected.toDate()))
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, inputCell)?.value).isEqualTo(expected)
    }

    @Test
    internal fun readStringCellWithLocaleTest() {
        val expected = DateTime().withTime(7, 21, 0, 0)
        val readerEnUs = DateTimeAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateTimeAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue(
            expected.toString(
                DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", "en_US".createLocale()))
            )
        )
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isEqualTo(expected)
        assertThatThrownBy { readerRuRu.read(attr, inputCell) }.isInstanceOf(SourceException::class.java)
        xlsTestDataCell.setCellValue(
            expected.toString(
                DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", "ru_RU".createLocale()))
            )
        )
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }.isInstanceOf(SourceException::class.java)
        assertThat(readerRuRu.read(attr, inputCell)?.value).isEqualTo(expected)
    }

    @Test
    internal fun readStringCellWithPatternTest() {
        val expected = DateTime().withTime(7, 21, 0, 0)
        val readerEnUs = DateTimeAttributeReader().apply { pattern = "M/d/YY h:m" }
        val readerRuRu = DateTimeAttributeReader().apply { pattern = "d.M.YY h:m" }
        xlsTestDataCell.setCellValue(expected.toString(DateTimeFormat.forPattern("M/d/YY h:m")))
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isEqualTo(expected)
        assertThatThrownBy { readerRuRu.read(attr, inputCell) }.isInstanceOf(SourceException::class.java)
        xlsTestDataCell.setCellValue(expected.toString(DateTimeFormat.forPattern("d.M.YY h:m")))
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }.isInstanceOf(SourceException::class.java)
        assertThat(readerRuRu.read(attr, inputCell)?.value).isEqualTo(expected)
    }

    @Test
    internal fun defaultDateReader() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[DateTimeType::class]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReaderWithPlugin::class.java)
        reader as AttributeReaderWithPlugin
        assertThat(reader.pluginClass).isEqualTo(DateTimeAttributeReader::class)
        val v = reader.pluginClass.createInstance() as DateTimeAttributeReader
        @Suppress("UNCHECKED_CAST")
        v.apply(reader.options as (DateTimeAttributeReader.() -> Unit))
        assertThat(v.locale).isEqualTo(DEFAULT_LOCALE_STR)
        assertThat(v.pattern).isEqualTo("d.M.yy H:m")
    }
}
