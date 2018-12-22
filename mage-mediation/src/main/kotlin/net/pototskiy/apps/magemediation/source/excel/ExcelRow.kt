package net.pototskiy.apps.magemediation.source.excel

import net.pototskiy.apps.magemediation.source.Row
import net.pototskiy.apps.magemediation.source.Sheet

class ExcelRow(private val row: org.apache.poi.ss.usermodel.Row) : Row {
    override val sheet: Sheet
        get() = ExcelSheet(row.sheet)
    override val rowNum: Int
        get() = row.rowNum

    override fun get(column: Int): ExcelCell? = row.getCell(column)?.let { ExcelCell(it) }
    override fun countCell(): Int = if (row.lastCellNum.toInt() == -1) 0 else row.lastCellNum.toInt()
    override fun iterator(): Iterator<ExcelCell> = ExcelCellIterator(row)
}