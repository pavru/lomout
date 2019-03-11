package net.pototskiy.apps.magemediation.api.source.workbook.excel

import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.Row
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
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
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> CellType.DOUBLE
                org.apache.poi.ss.usermodel.CellType.STRING -> CellType.STRING
                org.apache.poi.ss.usermodel.CellType.BLANK -> CellType.BLANK
                org.apache.poi.ss.usermodel.CellType.BOOLEAN -> CellType.BOOL
                else ->
                    throw SourceException(
                        "Unsupported cell type, " +
                                "(${row.sheet.workbook.name}:${row.sheet.name}:${address.row}:${address.column})"
                    )
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

    override fun setCellValue(value: String) = cell.setCellValue(value)

    override fun setCellValue(value: Boolean) = cell.setCellValue(value)

    override fun setCellValue(value: Long) = cell.setCellValue(value.toDouble())

    override fun setCellValue(value: Double) = cell.setCellValue(value)

    override fun setCellValue(value: DateTime) = cell.setCellValue(value.toDate())

    override fun asString(): String {
        val format = NumberFormat.getInstance().apply { isGroupingUsed = false }
        val type = if (cell.cellType == org.apache.poi.ss.usermodel.CellType.FORMULA) {
            cell.cachedFormulaResultType
        } else {
            cell.cellType
        }
        return when (type) {
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> format.format(cell.numericCellValue)
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
            org.apache.poi.ss.usermodel.CellType.BLANK -> ""
            org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> throw SourceException("${cellType.name} is not supported")
        }
    }
}
