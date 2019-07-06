package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.plugable.Writer
import net.pototskiy.apps.lomout.api.plugable.WriterBuilder
import net.pototskiy.apps.lomout.api.plugable.createWriter
import net.pototskiy.apps.lomout.api.source.nested.NestedAttributeWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class DefaultDocumentWriterTest {
    internal class NestedType : Document() {
        var attr1: String = ""
        var attr2: String = ""

        companion object : DocumentMetadata(NestedType::class)
    }

    internal class TestType : Document() {
        @Writer(AttrWriter::class)
        var attr: NestedType = NestedType()

        companion object : DocumentMetadata(TestType::class)

        class AttrWriter : WriterBuilder {
            override fun build(): AttributeWriter<out Any?> = createWriter<DocumentAttributeStringWriter> {
                quote = null
                delimiter = ','
                valueQuote = '"'
                valueDelimiter = '='
            }
        }
    }

    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var outputCell: Cell

    @BeforeEach
    internal fun setUp() {
        xlsWorkbook = HSSFWorkbookFactory.createWorkbook()
        val xlsSheet = xlsWorkbook.createSheet("test-data")
        xlsSheet.isActive = true
        xlsTestDataCell = xlsSheet.createRow(0).createCell(0)
        workbook = ExcelWorkbook(xlsWorkbook, false)
        outputCell = workbook["test-data"][0]!![0]!!
    }

    @AfterEach
    internal fun tearDown() {
        workbook.close()
    }

    @Test
    internal fun writeAttributeListToCellTest() {
        val wb = NestedAttributeWorkbook(null, ',', '"', '=', "test")
        wb.string = "attr1=value1,attr2=value2"
        val doc = NestedType().apply {
            attr1 = "value1"
            attr2 = "value2"
        }
        val writer = DocumentAttributeStringWriter().apply {
            delimiter = ','
            valueQuote = '"'
            valueDelimiter = '='
        }
        writer.write(doc, outputCell)
        assertThat(outputCell.stringValue).isEqualTo("attr1=value1,attr2=value2")
    }
}
