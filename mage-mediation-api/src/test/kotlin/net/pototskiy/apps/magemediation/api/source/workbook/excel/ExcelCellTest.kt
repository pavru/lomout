package net.pototskiy.apps.magemediation.api.source.workbook.excel

import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookFactory
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.io.File

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class ExcelCellTest {

    @Test
    internal fun cellBasicTest() {
        WorkbookFactory.create(
            File(
                "${System.getenv("TEST_DATA_DIR")}/excel-workbook-test.xls"
            ).toURI().toURL()
        ).use { workbook ->
            val sheet = workbook["Sheet2"]
            val row = sheet[0]!!
            assertThat(row[0]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[0]!!.doubleValue).isEqualTo(1.0)
            assertThat(row[1]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[1]!!.doubleValue).isEqualTo(2.0)
            assertThat(row[1]!!.longValue).isEqualTo(2L)
            assertThat(row[2]!!.cellType).isEqualTo(CellType.STRING)
            assertThat(row[2]!!.stringValue).isEqualTo("test")
            assertThat(row[3]!!.cellType).isEqualTo(CellType.BOOL)
            assertThat(row[3]!!.booleanValue).isEqualTo(true)
            assertThat(row.getOrEmptyCell(4).cellType).isEqualTo(CellType.BLANK)
            assertThat(row[5]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[5]!!.doubleValue).isEqualTo(3.0)
            assertThat(row[5]!!.address).isEqualTo(CellAddress(0, 5))
            assertThatThrownBy { row[6]!!.cellType }.isInstanceOf(SourceException::class.java)
        }
    }

    @Test
    internal fun setValueTest() {
        WorkbookFactory.create(
            File(
                "${System.getenv("TEST_DATA_DIR")}/excel-workbook-test.xls"
            ).toURI().toURL()
        ).use { workbook ->
            val sheet = workbook["Sheet2"]
            val row = sheet[0]!!
            val cell = row.getOrEmptyCell(100)
            val now = DateTime.now()
            cell.setCellValue(now)
            assertThat(cell.doubleValue).isEqualTo(HSSFDateUtil.getExcelDate(now.toDate()))
            cell.setCellValue("test")
            assertThat(cell.stringValue).isEqualTo("test")
            cell.setCellValue(111L)
            assertThat(cell.longValue).isEqualTo(111L)
            cell.setCellValue(11.22)
            assertThat(cell.doubleValue).isEqualTo(11.22)
            cell.setCellValue(true)
            assertThat(cell.booleanValue).isTrue()
        }
    }

    @Test
    internal fun cellAsStringTest() {
        WorkbookFactory.create(
            File(
                "${System.getenv("TEST_DATA_DIR")}/excel-workbook-test.xls"
            ).toURI().toURL()
        ).use { workbook ->
            val sheet = workbook["Sheet2"]
            val row = sheet[0]!!
            assertThat(row[0]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[0]!!.asString()).isEqualTo("1")
            assertThat(row[1]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[1]!!.asString()).isEqualTo("2")
            assertThat(row[2]!!.cellType).isEqualTo(CellType.STRING)
            assertThat(row[2]!!.asString()).isEqualTo("test")
            assertThat(row[3]!!.cellType).isEqualTo(CellType.BOOL)
            assertThat(row[3]!!.asString()).isEqualTo("true")
            assertThat(row.getOrEmptyCell(4).asString()).isEqualTo("")
            assertThat(row[5]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[5]!!.asString()).isEqualTo("3")
            assertThat(row[5]!!.address).isEqualTo(CellAddress(0, 5))
            assertThatThrownBy { row[6]!!.asString() }.isInstanceOf(SourceException::class.java)
        }
    }

    companion object {
        val testDataForWrite = listOf(
            listOf("header1", "header2", "header3"),
            listOf("11", "12", "13"),
            listOf("21", "22", "23")
        )
    }
}
