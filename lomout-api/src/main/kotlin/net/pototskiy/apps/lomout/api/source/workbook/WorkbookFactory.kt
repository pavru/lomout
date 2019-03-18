package net.pototskiy.apps.lomout.api.source.workbook

import net.pototskiy.apps.lomout.api.AppWorkbookException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvInputWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvOutputWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.setFileName
import org.apache.commons.csv.CSVFormat
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.net.URL
import java.util.*

class WorkbookFactory {
    companion object {
        fun create(source: URL, workbookLocale: Locale = DEFAULT_LOCALE, forInput: Boolean = true): Workbook {
            val fileName = File(source.file).name
            val file = File(fileName)
            return when (file.extension.toLowerCase()) {
                "xls" -> {
                    val wb = hssfWorkbook(forInput, source)
                    wb.setFileName(File(source.file))
                    ExcelWorkbook(wb, forInput)
                }
                "xlsx" -> {
                    val wb = xssfWorkbook(forInput, source)
                    wb.setFileName(File(source.file))
                    ExcelWorkbook(wb, forInput)
                }
                "csv" -> {
                    val format = CSVFormat.RFC4180.withEscape('\\')
                    if (forInput)
                        CsvInputWorkbook(source, format, workbookLocale)
                    else
                        CsvOutputWorkbook(source, format, workbookLocale)
                }
                else ->
                    throw AppWorkbookException("Unsupported file format, file: $fileName")
            }
        }

        private fun xssfWorkbook(forInput: Boolean, input: URL): org.apache.poi.ss.usermodel.Workbook =
            if (forInput) {
                SXSSFWorkbook(XSSFWorkbook(input.openStream())).also {
                    XSSFFormulaEvaluator.evaluateAllFormulaCells(it)
                }
            } else {
                XSSFWorkbook()
            }

        private fun hssfWorkbook(
            forInput: Boolean,
            input: URL
        ): HSSFWorkbook = if (forInput) {
            HSSFWorkbook(input.openStream()).also {
                HSSFFormulaEvaluator.evaluateAllFormulaCells(it)
            }
        } else {
            HSSFWorkbook()
        }
    }
}
