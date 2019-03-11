package net.pototskiy.apps.magemediation.api.source.workbook.excel

import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.Row
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet

class ExcelRow(private val row: org.apache.poi.ss.usermodel.Row) :
    Row {
    override fun insertCell(column: Int): Cell {
        return ExcelCell(row.createCell(column))
    }

    override fun getOrEmptyCell(column: Int): Cell {
        return get(column) ?: ExcelCell(row.createCell(column))
    }

    override val sheet: Sheet
        get() = ExcelSheet(row.sheet)
    override val rowNum: Int
        get() = row.rowNum

    override fun get(column: Int): ExcelCell? = row.getCell(column)?.let {
        ExcelCell(
            it
        )
    }
    override fun countCell(): Int = if (row.lastCellNum.toInt() == -1) 0 else row.lastCellNum.toInt()
    override fun iterator(): Iterator<ExcelCell> =
        ExcelCellIterator(row)
}
