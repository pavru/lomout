package net.pototskiy.apps.magemediation.api.source.workbook

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.api.source.workbook.csv.CsvWorkbook
import net.pototskiy.apps.magemediation.api.source.workbook.excel.ExcelWorkbook
import net.pototskiy.apps.magemediation.api.source.workbook.excel.setFileName
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
        fun create(source: URL, workbookLocale: Locale = DEFAULT_LOCALE): Workbook {
            val fileName = File(source.file).name
            val input = source.openStream()
            val file = File(fileName)
            return when (file.extension.toLowerCase()) {
                "xls" -> {
                    val wb = HSSFWorkbook(input)
                    HSSFFormulaEvaluator.evaluateAllFormulaCells(wb)
                    wb.setFileName(fileName)
                    ExcelWorkbook(wb)
                }
                "xlsx" -> {
                    val wb = SXSSFWorkbook(XSSFWorkbook(input))
                    XSSFFormulaEvaluator.evaluateAllFormulaCells(wb)
                    wb.setFileName(fileName)
                    ExcelWorkbook(wb)
                }
                "csv" -> {
                    val format = CSVFormat.RFC4180.withEscape('\\')
                    CsvWorkbook(source, format, workbookLocale)
                }
                else ->
                    throw SourceException("Unsupported file format, file: $fileName")
            }
        }
    }
}
