package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.SupportAttributeType
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvCell
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvInputWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.commons.csv.CSVFormat
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

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DefaultLongReaderTest {
    internal class TestType : Document() {
        var attr: Long = 0L

        companion object : DocumentMetadata(TestType::class)
    }

    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private var attr = TestType.attributes.getValue("attr")
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell

    @BeforeEach
    internal fun setUp() {
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
    internal fun readBlankCellTest() {
        val reader = LongAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setBlank()
        assertThat(reader.read(attr, inputCell)).isNull()
    }

    @Test
    internal fun readDoubleCellTest() {
        val reader = LongAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(2.0)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(reader.read(attr, inputCell)).isEqualTo(2)
        xlsTestDataCell.setCellValue(2.2)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThatThrownBy { reader.read(attr, inputCell) }.isInstanceOf(TypeCastException::class.java)
    }

    @Test
    internal fun readLongCellTest() {
        val reader = LongAttributeReader().apply { locale = "en_US" }
        val cell = createCsvCell("11")
        assertThat(cell.cellType).isEqualTo(CellType.LONG)
        assertThat(reader.read(attr, cell)).isEqualTo(11)
    }

    @Test
    internal fun readBooleanCellTest() {
        val reader = LongAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(true)
        assertThat(inputCell.cellType).isEqualTo(CellType.BOOL)
        assertThat(reader.read(attr, inputCell)).isEqualTo(1)
        xlsTestDataCell.setCellValue(false)
        assertThat(inputCell.cellType).isEqualTo(CellType.BOOL)
        assertThat(reader.read(attr, inputCell)).isEqualTo(0)
    }

    @Test
    internal fun readStringEnUsCellTest() {
        val readerEnUs = LongAttributeReader().apply { locale = "en_US" }
        val readerRuRu = LongAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue("11")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)).isEqualTo(11L)
        assertThat(readerRuRu.read(attr, inputCell)).isEqualTo(11L)
        @Suppress("GraziInspection", "SpellCheckingInspection")
        xlsTestDataCell.setCellValue("xxxx")
        assertThatThrownBy {
            readerEnUs.read(attr, inputCell)
        }.isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("String cannot be parsed to long.")
    }

    @Test
    internal fun defaultLongReaderTest() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[SupportAttributeType.longType]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReader::class.java)
        reader as LongAttributeReader
        assertThat(reader.locale).isEqualTo(DEFAULT_LOCALE_STR)
    }

    private fun createCsvCell(value: String): CsvCell {
        val reader = value.byteInputStream().reader()
        CsvInputWorkbook(reader, CSVFormat.RFC4180).use {
            return it[0][0][0]!!
        }
    }
}
