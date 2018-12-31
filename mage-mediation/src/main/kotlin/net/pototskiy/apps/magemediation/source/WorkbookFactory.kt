package net.pototskiy.apps.magemediation.source

import net.pototskiy.apps.magemediation.loader.LoaderException
import net.pototskiy.apps.magemediation.source.csv.CsvWorkbook
import net.pototskiy.apps.magemediation.source.excel.ExcelWorkbook
import net.pototskiy.apps.magemediation.source.excel.setFileName
import org.apache.commons.csv.CSVFormat
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.net.URL

class WorkbookFactory {
    companion object {
        fun create(source: URL): Workbook {
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
                    val wb = XSSFWorkbook(input)
                    XSSFFormulaEvaluator.evaluateAllFormulaCells(wb)
                    wb.setFileName(fileName)
                    ExcelWorkbook(wb)
                }
                "csv" -> {
                    val format = CSVFormat.RFC4180.withEscape('\\')
                    CsvWorkbook(source, format)
                }
                else ->
                    throw LoaderException("Unsupported file format, file: $fileName")
            }
        }
    }
}