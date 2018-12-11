package importer

import Args
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

object MyWorkbook {
    private var _workbook: Workbook? = null
    val workbook: Workbook
        get() {
            val v = _workbook
            return if (v != null) {
                v
            } else {
                val file = File(Args.files[0])
                _workbook = when {
                    file.extension.toLowerCase() == "xls" -> {
                        val v = HSSFWorkbook(FileInputStream(Args.files[0]))
                        HSSFFormulaEvaluator.evaluateAllFormulaCells(v)
                        v
                    }
                    file.extension.toLowerCase() == "xlsx" -> {
                        val v = XSSFWorkbook(FileInputStream(Args.files[0]))
                        XSSFFormulaEvaluator.evaluateAllFormulaCells(v)
                        v
                    }
                    else -> throw WrongFileFormat("This Excel file format does not support")
                }
                _workbook as Workbook
            }
        }
}