package net.pototskiy.apps.magemediation.api.source.workbook.csv

import net.pototskiy.apps.magemediation.api.CSV_SHEET_NAME
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookFactory
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.io.File
import java.util.*

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class CsvWorkbookTest {
    private lateinit var file: File

    @BeforeEach
    internal fun setUp() {
        val baseFile = File("${System.getenv("TEST_DATA_DIR")}/csv-workbook-test.csv")
        file = File("../tmp/${UUID.randomUUID()}.csv")
        baseFile.copyTo(file)
    }

    @AfterEach
    internal fun tearDown() {
        file.delete()
    }

    @Test
    internal fun workbookBasicTest() {
        WorkbookFactory.create(file.toURI().toURL(), "en_US".createLocale()).use { workbook ->
            assertThat(workbook).isInstanceOf(CsvInputWorkbook::class.java)
            assertThat(workbook.type).isEqualTo(WorkbookType.CSV)
            assertThat(workbook.name).isEqualTo(file.name)
            assertThat(workbook.hasSheet("Sheet1")).isFalse()
            assertThat(workbook.hasSheet(CSV_SHEET_NAME)).isTrue()
            assertThat(workbook[CSV_SHEET_NAME]).isNotNull.isInstanceOf(CsvSheet::class.java)
            assertThat(workbook[CSV_SHEET_NAME].name).isEqualTo(workbook[0].name)
            assertThatThrownBy { workbook["Sheet1"] }.isInstanceOf(SourceException::class.java)
            assertThatThrownBy { workbook[1] }.isInstanceOf(SourceException::class.java)
            assertThatThrownBy { workbook.insertSheet("test") }.isInstanceOf(SourceException::class.java)
        }
        val tmpFile = File.createTempFile("csv-test", ".csv", File("../tmp/"))
        try {
            WorkbookFactory.create(tmpFile.toURI().toURL(), "en_US".createLocale(), false).use { workbook ->
                assertThat(workbook.insertSheet(CSV_SHEET_NAME)).isNotNull
                assertThatThrownBy { workbook.insertSheet("test") }.isInstanceOf(SourceException::class.java)
            }
        } finally {
            tmpFile.delete()
        }
    }
}
