package net.pototskiy.apps.magemediation.api.source.workbook.excel

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
            val sheet = workbook.insertSheet("default")
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
                val sheet = wb.getSheet("default")
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
            val sheet = workbook.insertSheet("default")
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
                val sheet = wb.getSheet("default")
                for ((rowNum, row) in sheet.withIndex()) {
                    assertThat(row.map { it.stringCellValue }).containsExactlyElementsOf(testDataForWrite[rowNum])
                }
            }
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
