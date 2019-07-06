package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
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

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class LM35DefaultDocumentAttributeReaderTest {
    internal class NestedType : Document() {
        var attr1: String = ""
        var attr2: String = ""

        companion object : DocumentMetadata(NestedType::class)
    }

    internal class TestType : Document() {
        var attr: NestedType = NestedType()

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
    internal fun readeAttributeListFromBlankTest() {
        val reader = DocumentAttributeReader().apply {
            delimiter = ','
            quote = null
            valueDelimiter = '='
            valueQuote = '\''
        }
        xlsTestDataCell.setBlank()
        assertThat(reader.read(attr, inputCell)).isNull()
    }

    @Test
    internal fun readAttributeListDoubleCellTest() {
        val reader = DocumentAttributeReader().apply {
            delimiter = ','
            quote = null
            valueDelimiter = '='
            valueQuote = '\''
        }
        xlsTestDataCell.setCellValue(1.1)
        assertThatThrownBy { reader.read(attr, inputCell) }.isInstanceOf(AppDataException::class.java)
    }
}
