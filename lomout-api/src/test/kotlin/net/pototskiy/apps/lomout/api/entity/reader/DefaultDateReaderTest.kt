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
import net.pototskiy.apps.lomout.api.entity.type.DATE
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

@Execution(ExecutionMode.CONCURRENT)
internal class DefaultDateReaderTest {
    private val typeManager = EntityTypeManagerImpl()
    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<DATE>
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell

    @BeforeEach
    internal fun setUp() {
        @Suppress("UNCHECKED_CAST")
        attr = typeManager.createAttribute(
            "attr", DATE::class,
            builder = null,
            reader = defaultReaders[DATE::class] as AttributeReader<out DATE>,
            writer = defaultWriters[DATE::class] as AttributeWriter<out DATE>
        )
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
        val expected = DateTimeFormat.forPattern("d.M.YY").parseDateTime("15.03.31")
        val readerEnUs = DateAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue(HSSFDateUtil.getExcelDate(expected.toDate()))
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, inputCell)?.value).isEqualTo(expected)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, inputCell)?.value).isEqualTo(expected)
    }

    @Test
    internal fun readLongCellTest() {
        val expected = DateTimeFormat.forPattern("d.M.YY").parseDateTime("15.03.31")
        val readerEnUs = DateAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateAttributeReader().apply { locale = "ru_RU" }
        val cell = createCsvCell(expected.millis.toString())
        assertThat(cell.cellType).isEqualTo(CellType.LONG)
        assertThat(readerEnUs.read(attr, cell)?.value).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, cell)?.value).isEqualTo(expected)
        assertThat(cell.cellType).isEqualTo(CellType.LONG)
        assertThat(readerEnUs.read(attr, cell)?.value).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, cell)?.value).isEqualTo(expected)
    }

    @Test
    internal fun readBooleanCellTest() {
        val readerEnUs = DateAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateAttributeReader().apply { locale = "ru_RU" }
        val readerWithPattern = DateAttributeReader().apply { pattern = "d.M.yy" }
        xlsTestDataCell.setCellValue(true)
        assertThat(inputCell.cellType).isEqualTo(CellType.BOOL)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isNull()
        assertThat(readerRuRu.read(attr, inputCell)?.value).isNull()
        assertThat(readerWithPattern.read(attr, inputCell)?.value).isNull()
    }

    @Test
    internal fun readStringCellTest() {
        val expected = DateTime().withTime(0, 0, 0, 0)
        val readerEnUs = DateAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue(
            expected.toString(
                DateTimeFormat.forPattern(
                    DateTimeFormat.patternForStyle("S-", "en_US".createLocale())
                )
            )
        )
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isEqualTo(expected)
        assertThatThrownBy { readerRuRu.read(attr, inputCell) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("String cannot be converted to date with the locale 'ru_RU'.")
        xlsTestDataCell.setCellValue(
            expected.toString(
                DateTimeFormat.forPattern(
                    DateTimeFormat.patternForStyle("S-", "ru_RU".createLocale())
                )
            )
        )
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("String cannot be converted to date with the locale")
        assertThat(readerRuRu.read(attr, inputCell)?.value).isEqualTo(expected)
    }

    @Test
    internal fun readStringCellWithPatternTest() {
        val expected = DateTime().withTime(0, 0, 0, 0)
        val readerEnUs = DateAttributeReader().apply { pattern = "M/d/YY" }
        val readerRuRu = DateAttributeReader().apply { pattern = "d.M.YY" }
        xlsTestDataCell.setCellValue(expected.toString(DateTimeFormat.forPattern("M/d/YY")))
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isEqualTo(expected)
        assertThatThrownBy { readerRuRu.read(attr, inputCell) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("String cannot be converted to date with the pattern")
        xlsTestDataCell.setCellValue(expected.toString(DateTimeFormat.forPattern("d.M.YY")))
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }.isInstanceOf(AppDataException::class.java)
        assertThat(readerRuRu.read(attr, inputCell)?.value).isEqualTo(expected)
    }

    @Test
    internal fun defaultDateReaderTest() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[DATE::class]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReaderWithPlugin::class.java)
        reader as AttributeReaderWithPlugin
        assertThat(reader.pluginClass).isEqualTo(DateAttributeReader::class)
        val v = reader.pluginClass.createInstance() as DateAttributeReader
        @Suppress("UNCHECKED_CAST")
        v.apply(reader.options as (DateAttributeReader.() -> Unit))
        assertThat(v.locale).isEqualTo(DEFAULT_LOCALE_STR)
        assertThat(v.pattern).isEqualTo("d.M.yy")
    }

    private fun createCsvCell(value: String): CsvCell {
        val reader = value.byteInputStream().reader()
        CsvInputWorkbook(reader, CSVFormat.RFC4180).use {
            return it[0][0][0]!!
        }
    }
}
