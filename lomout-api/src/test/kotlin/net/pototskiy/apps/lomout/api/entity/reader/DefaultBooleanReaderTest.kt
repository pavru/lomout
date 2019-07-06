package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
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
internal class DefaultBooleanReaderTest {

    internal class TestType : Document() {
        var attr: Boolean = false

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
    internal fun readDoubleCellTest() {
        val reader = BooleanAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(1.0)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(reader.read(attr, inputCell)).isEqualTo(true)
        xlsTestDataCell.setCellValue(0.0)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(reader.read(attr, inputCell)).isEqualTo(false)
    }

    @Test
    internal fun readLongCellTest() {
        val reader = BooleanAttributeReader().apply { locale = "en_US" }
        var cell = createCsvCell(1L.toString())
        assertThat(cell.cellType).isEqualTo(CellType.LONG)
        assertThat(reader.read(attr, cell)).isEqualTo(true)
        cell = createCsvCell(0L.toString())
        assertThat(cell.cellType).isEqualTo(CellType.LONG)
        assertThat(reader.read(attr, cell)).isEqualTo(false)
    }

    @Test
    internal fun readBooleanCellTest() {
        val reader = BooleanAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(true)
        assertThat(inputCell.cellType).isEqualTo(CellType.BOOL)
        assertThat(reader.read(attr, inputCell)).isEqualTo(true)
        xlsTestDataCell.setCellValue(false)
        assertThat(inputCell.cellType).isEqualTo(CellType.BOOL)
        assertThat(reader.read(attr, inputCell)).isEqualTo(false)
    }

    @Test
    internal fun readStringEnUsCorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue("true")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)).isEqualTo(true)
        xlsTestDataCell.setCellValue("false")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)).isEqualTo(false)
    }

    @Test
    internal fun readStringEnUsIncorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue("not boolean")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Value 'not boolean' cannot be converted to boolean.")
    }

    @Test
    internal fun readStringRuRuCorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue("истИНа")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)).isEqualTo(true)
        xlsTestDataCell.setCellValue("Ложь")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)).isEqualTo(false)
    }

    @Test
    internal fun readStringRuRuIncorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue("какая-то строка")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Value 'какая-то строка' cannot be converted to boolean.")
    }

    @Test
    internal fun readBlankCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setBlank()
        assertThat(readerEnUs.read(attr, inputCell)).isNull()
    }

    @Test
    internal fun defaultBooleanReaderTest() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[SupportAttributeType.booleanType]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReader::class.java)
        @Suppress("UNCHECKED_CAST")
        reader as AttributeReader<Boolean>
    }

    @Test
    internal fun defaultBooleanListReader() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[SupportAttributeType.booleanListType]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReader::class.java)
        @Suppress("UNCHECKED_CAST")
        reader as AttributeReader<List<Boolean>>
    }
    private fun createCsvCell(value: String): CsvCell {
        val reader = value.byteInputStream().reader()
        CsvInputWorkbook(reader, CSVFormat.RFC4180).use {
            return it[0][0][0]!!
        }
    }

}
