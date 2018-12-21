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

class WorkbookFactory {
    companion object {
        fun create(fileName: String): Workbook {
            val file = File(fileName)
            return when (file.extension.toLowerCase()) {
                "xls" -> {
                    val wb = HSSFWorkbook(file.inputStream())
                    HSSFFormulaEvaluator.evaluateAllFormulaCells(wb)
                    wb.setFileName(fileName)
                    ExcelWorkbook(wb)
                }
                "xlsx" -> {
                    val wb = XSSFWorkbook(file.inputStream())
                    XSSFFormulaEvaluator.evaluateAllFormulaCells(wb)
                    wb.setFileName(fileName)
                    ExcelWorkbook(wb)
                }
                "csv" -> {
                    val format = CSVFormat.RFC4180
                    CsvWorkbook(file, format, fileName)
                }
                else ->
                    throw LoaderException("Unsupported file format, file: $fileName")
            }
        }
    }
}