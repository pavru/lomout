package net.pototskiy.apps.magemediation.api.source.workbook.excel

import net.pototskiy.apps.magemediation.api.CSV_SHEET_NAME
import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookFactory
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookType
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import java.io.File

@Suppress("MagicNumber")
internal class ExcelWorkbookTest {
    @Test
    internal fun createSimpleXlsFileTest() {
        val file = File("../tmp/csv-creation-test.xls")
        file.parentFile.mkdirs()
        file.delete()
        assertThat(file.exists()).isFalse()
        WorkbookFactory.create(file.toURI().toURL(), DEFAULT_LOCALE, false).use { workbook ->
            val sheet = workbook.insertSheet(CSV_SHEET_NAME)
            for ((rowNum, list) in testDataForWrite.withIndex()) {
                val row = sheet.insertRow(rowNum)
                list.forEachIndexed { c, v ->
                    row.insertCell(c).setCellValue(v)
                }
            }
        }
        assertThat(file.exists()).isTrue()
        file.inputStream().use { reader ->
            HSSFWorkbook(reader).use { wb ->
                val sheet = wb.getSheet(CSV_SHEET_NAME)
                for ((rowNum, row) in sheet.withIndex()) {
                    assertThat(row.map { it.stringCellValue }).containsExactlyElementsOf(testDataForWrite[rowNum])
                }
            }
        }
    }

    @Test
    internal fun createSimpleXlsxFileTest() {
        val file = File("../tmp/csv-creation-test.xlsx")
        file.parentFile.mkdirs()
        file.delete()
        assertThat(file.exists()).isFalse()
        WorkbookFactory.create(file.toURI().toURL(), DEFAULT_LOCALE, false).use { workbook ->
            val sheet = workbook.insertSheet(CSV_SHEET_NAME)
            for ((rowNum, list) in testDataForWrite.withIndex()) {
                val row = sheet.insertRow(rowNum)
                list.forEachIndexed { c, v ->
                    row.insertCell(c).setCellValue(v)
                }
            }
        }
        assertThat(file.exists()).isTrue()
        file.inputStream().use { reader ->
            XSSFWorkbook(reader).use { wb ->
                val sheet = wb.getSheet(CSV_SHEET_NAME)
                for ((rowNum, row) in sheet.withIndex()) {
                    assertThat(row.map { it.stringCellValue }).containsExactlyElementsOf(testDataForWrite[rowNum])
                }
            }
        }
    }

    @Test
    internal fun workbookBasicTest() {
        WorkbookFactory.create(
            File(
                "${System.getenv("TEST_DATA_DIR")}/excel-workbook-test.xls"
            ).toURI().toURL()
        ).use { workbook ->
            assertThat(workbook).isInstanceOf(ExcelWorkbook::class.java)
            assertThat(workbook.type).isEqualTo(WorkbookType.EXCEL)
            assertThat(workbook.name).contains("excel-workbook-test.xls")
            assertThat(workbook.hasSheet("Sheet1")).isTrue()
            assertThat(workbook.hasSheet("Sheet2")).isTrue()
            assertThat(workbook.hasSheet("Sheet5")).isFalse()
            assertThat(workbook["Sheet1"]).isInstanceOf(ExcelSheet::class.java)
            assertThat(workbook["Sheet1"].name).isEqualTo(workbook[0].name)
        }
    }

    @Test
    internal fun sheetBasicTest() {
        WorkbookFactory.create(
            File(
                "${System.getenv("TEST_DATA_DIR")}/excel-workbook-test.xls"
            ).toURI().toURL()
        ).use { workbook ->
            val sheet1 = workbook["Sheet1"]
            val sheet3 = workbook["Sheet3"]
            assertThat(sheet1.name).isEqualTo("Sheet1")
            assertThat(sheet1.workbook.name).isEqualTo(workbook.name)
            assertThat(sheet1[0]).isNotNull.isInstanceOf(ExcelRow::class.java)
            assertThat(sheet3[0]).isNull()
        }
    }

    @Test
    internal fun rowBasicTest() {
        WorkbookFactory.create(
            File(
                "${System.getenv("TEST_DATA_DIR")}/excel-workbook-test.xls"
            ).toURI().toURL()
        ).use { workbook ->
            val sheet = workbook["Sheet1"]
            val row = sheet[0]
            assertThat(row).isNotNull.isInstanceOf(ExcelRow::class.java)
            assertThat(row?.sheet?.name).isEqualTo(sheet.name)
            assertThat(row?.countCell()).isEqualTo(3)
            assertThat(row!![0]).isNotNull.isInstanceOf(ExcelCell::class.java)
            assertThat(row[5]).isNull()
            assertThat(row.getOrEmptyCell(5)).isNotNull.isInstanceOf(ExcelCell::class.java)
        }
    }
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
            Assertions.assertThatThrownBy { row[6]!!.cellType }.isInstanceOf(SourceException::class.java)
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
            Assertions.assertThatThrownBy { row[6]!!.asString() }.isInstanceOf(SourceException::class.java)
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
