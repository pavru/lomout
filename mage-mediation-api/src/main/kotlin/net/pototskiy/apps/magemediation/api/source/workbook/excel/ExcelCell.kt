package net.pototskiy.apps.magemediation.api.source.workbook.excel

import net.pototskiy.apps.magemediation.api.source.workbook.*
import org.joda.time.DateTime
import java.text.NumberFormat

class ExcelCell(private val cell: org.apache.poi.ss.usermodel.Cell) : Cell {
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
                    throw SourceException("Unsupported cell type, workbook: ${row.sheet.workbook.name}, sheet: ${row.sheet.name}, cell: ${address.row},${address.column}")
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> CellType.DOUBLE
                org.apache.poi.ss.usermodel.CellType.STRING -> CellType.STRING
                org.apache.poi.ss.usermodel.CellType.FORMULA ->
                    throw SourceException("Unsupported cell type, workbook: ${row.sheet.workbook.name}, sheet: ${row.sheet.name}, cell: ${address.row},${address.column}")
                org.apache.poi.ss.usermodel.CellType.BLANK -> CellType.BLANK
                org.apache.poi.ss.usermodel.CellType.BOOLEAN -> CellType.BOOL
                org.apache.poi.ss.usermodel.CellType.ERROR ->
                    throw SourceException("Unsupported cell type, workbook: ${row.sheet.workbook.name}, sheet: ${row.sheet.name}, cell: ${address.row},${address.column}")
                else ->
                    throw SourceException("Unsupported cell type, workbook: ${row.sheet.workbook.name}, sheet: ${row.sheet.name}, cell: ${address.row},${address.column}")
            }
        }
    override val booleanValue: Boolean
        get() = cell.booleanCellValue
    override val longValue: Long
        get() = cell.numericCellValue.toLong()
    override val doubleValue: Double
        get() = cell.numericCellValue
    override val stringValue: String
        get() = cell.stringCellValue
    override val row: Row
        get() = ExcelRow(cell.row)

    override fun setCellValue(value: String) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Boolean) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Long) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Double) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: DateTime) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun asString(): String {
        val format = NumberFormat.getInstance().apply {
            isGroupingUsed = false
        }
        return when (cell.cellType) {
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> format.format(cell.numericCellValue)
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
            org.apache.poi.ss.usermodel.CellType.BLANK -> ""
            org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
            org.apache.poi.ss.usermodel.CellType.FORMULA -> when (cell.cachedFormulaResultType) {
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
