package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.AttributeReader
import net.pototskiy.apps.lomout.api.entity.AttributeReaderWithPlugin
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvCell
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvInputWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.commons.csv.CSVFormat
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
    private val typeManager = EntityTypeManagerImpl()
    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<DATETIME>
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell

    @BeforeEach
    internal fun setUp() {
        @Suppress("UNCHECKED_CAST")
        attr = typeManager.createAttribute(
            "attr", DATETIME::class,
            builder = null,
            reader = defaultReaders[DATETIME::class] as AttributeReader<out DATETIME>,
            writer = defaultWriters[DATETIME::class] as AttributeWriter<out DATETIME>
        )
        entity = typeManager.createEntityType("test", false).also {
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
    internal fun readLongCellTest() {
        val expected = DateTime().withTime(7, 21, 0, 0)
        val readerEnUs = DateTimeAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateTimeAttributeReader().apply { locale = "ru_RU" }
        val readerWithPattern = DateTimeAttributeReader().apply { pattern = "d.M.yy h:m" }
        val cell = createCsvCell(expected.millis.toString())
        assertThat(cell.cellType).isEqualTo(CellType.LONG)
        assertThat(readerEnUs.read(attr, cell)?.value).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, cell)?.value).isEqualTo(expected)
        assertThat(readerWithPattern.read(attr, cell)?.value).isEqualTo(expected)
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
        assertThatThrownBy { readerRuRu.read(attr, inputCell) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("String cannot be converted to date-time with the locale")
        xlsTestDataCell.setCellValue(
            expected.toString(
                DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", "ru_RU".createLocale()))
            )
        )
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }.isInstanceOf(AppDataException::class.java)
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
        assertThatThrownBy { readerRuRu.read(attr, inputCell) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("String cannot be converted to date with the pattern")
        xlsTestDataCell.setCellValue(expected.toString(DateTimeFormat.forPattern("d.M.YY h:m")))
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }.isInstanceOf(AppDataException::class.java)
        assertThat(readerRuRu.read(attr, inputCell)?.value).isEqualTo(expected)
    }

    @Test
    internal fun defaultDateReaderTest() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[DATETIME::class]
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

    private fun createCsvCell(value: String): CsvCell {
        val reader = value.byteInputStream().reader()
        CsvInputWorkbook(reader, CSVFormat.RFC4180).use {
            return it[0][0][0]!!
        }
    }
}
