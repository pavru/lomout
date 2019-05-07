package net.pototskiy.apps.lomout.api.source.workbook.excel

import net.pototskiy.apps.lomout.api.AppCellException
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Row
import org.joda.time.DateTime
import java.text.NumberFormat

/**
 * Excel source cell
 *
 * @property cell Cell
 * @property address CellAddress
 * @property cellType CellType
 * @property booleanValue Boolean
 * @property longValue Long
 * @property doubleValue Double
 * @property stringValue String
 * @property row Row
 * @constructor
 */
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
                    throw AppCellException(
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

    /**
     * Set string cell value
     *
     * @param value String
     */
    override fun setCellValue(value: String) = cell.setCellValue(value)

    /**
     * Set boolean cell value
     *
     * @param value Boolean
     */
    override fun setCellValue(value: Boolean) = cell.setCellValue(value)

    /**
     * Set long cell value
     *
     * @param value Long
     */
    override fun setCellValue(value: Long) = cell.setCellValue(value.toDouble())

    /**
     * Set double cell value
     *
     * @param value Double
     */
    override fun setCellValue(value: Double) = cell.setCellValue(value)

    /**
     * Set cell [DateTime] value
     *
     * @param value DateTime
     */
    override fun setCellValue(value: DateTime) = cell.setCellValue(value.toDate())

    /**
     * Get excel cell value in string presentation
     *
     * @return String The string presentation of cell value
     */
    override fun asString(): String {
        val format = NumberFormat.getInstance().apply { isGroupingUsed = false }
        return when (if (cell.cellType == org.apache.poi.ss.usermodel.CellType.FORMULA) {
            cell.cachedFormulaResultType
        } else {
            cell.cellType
        }) {
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> format.format(cell.numericCellValue)
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
            org.apache.poi.ss.usermodel.CellType.BLANK -> ""
            org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> throw AppCellException("${cellType.name} is not supported")
        }
    }
}
