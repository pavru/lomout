package net.pototskiy.apps.lomout.api.source.workbook.csv

import net.pototskiy.apps.lomout.api.CSV_SHEET_NAME
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import org.apache.commons.csv.CSVFormat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.io.File

@Execution(ExecutionMode.CONCURRENT)
internal class CsvOutputWorkbookTest {
    @Test
    internal fun createSimpleCsvFileTest() {
        val file = File("../tmp/csv-creation-test.csv")
        file.parentFile.mkdirs()
        file.delete()
        assertThat(file.exists()).isEqualTo(false)
        WorkbookFactory.create(file.toURI().toURL(), DEFAULT_LOCALE, false).use { workbook ->
            val sheet = workbook.insertSheet(CSV_SHEET_NAME)
            for (list in testDataForWrite) {
                val row = sheet.insertRow(0)
                list.forEachIndexed { c, v ->
                    row.insertCell(c).setCellValue(v)
                }
            }
        }
        assertThat(file.exists()).isEqualTo(true)
        file.reader().use { reader ->
            CSVFormat.RFC4180.parse(reader).forEachIndexed { r, csvRecord ->
                assertThat(csvRecord.map { it }).containsExactlyElementsOf(testDataForWrite[r])
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
