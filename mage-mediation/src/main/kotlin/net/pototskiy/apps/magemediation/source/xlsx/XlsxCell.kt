package net.pototskiy.apps.magemediation.source.xlsx

import net.pototskiy.apps.magemediation.loader.*
import net.pototskiy.apps.magemediation.source.*
import org.apache.poi.xssf.usermodel.XSSFCell
import java.text.NumberFormat

class XlsxCell(private val cell: XSSFCell) : Cell {
    override val address: CellAddress
        get() = CellAddress(cell.rowIndex, cell.columnIndex)
    override val cellType: CellType
        get() {
            val type = if (cell.cellType == org.apache.poi.ss.usermodel.CellType.FORMULA)
                cell.cachedFormulaResultType
            else
                cell.cellType
            return when (type) {
                org.apache.poi.ss.usermodel.CellType._NONE ->
                    throw LoaderException("Unsupported cell type, workbook: ${row.sheet.workbook.name}, sheet: ${row.sheet.name}, cell: ${address.row},${address.column}")
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> CellType.DOUBLE
                org.apache.poi.ss.usermodel.CellType.STRING -> CellType.STRING
                org.apache.poi.ss.usermodel.CellType.FORMULA ->
                    throw LoaderException("Unsupported cell type, workbook: ${row.sheet.workbook.name}, sheet: ${row.sheet.name}, cell: ${address.row},${address.column}")
                org.apache.poi.ss.usermodel.CellType.BLANK -> CellType.STRING
                org.apache.poi.ss.usermodel.CellType.BOOLEAN -> CellType.BOOL
                org.apache.poi.ss.usermodel.CellType.ERROR ->
                    throw LoaderException("Unsupported cell type, workbook: ${row.sheet.workbook.name}, sheet: ${row.sheet.name}, cell: ${address.row},${address.column}")
                else ->
                    throw LoaderException("Unsupported cell type, workbook: ${row.sheet.workbook.name}, sheet: ${row.sheet.name}, cell: ${address.row},${address.column}")
            }
        }
    override val booleanValue: Boolean
        get() = cell.booleanCellValue
    override val intValue: Long
        get() = cell.numericCellValue.toLong()
    override val doubleValue: Double
        get() = cell.numericCellValue
    override val stringValue: String
        get() = cell.stringCellValue
    override val row: Row
        get() = XlsxRow(cell.row)

    override fun asString(): String {
        val format = NumberFormat.getInstance().apply {
            isGroupingUsed = false
        }
        return when(cell.cellType){
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> format.format(cell.numericCellValue)
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
            org.apache.poi.ss.usermodel.CellType.BLANK -> ""
            org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
            org.apache.poi.ss.usermodel.CellType.FORMULA -> when(cell.cachedFormulaResultType){
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> format.format(cell.numericCellValue)
                org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
                org.apache.poi.ss.usermodel.CellType.BLANK -> ""
                org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
                else -> throw SourceException("${cellType.name} is not supported")
            }
            else -> throw SourceException("${cellType.name} is not supported")
        }
    }
}