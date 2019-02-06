package net.pototskiy.apps.magemediation.source.csv

import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellAddress
import net.pototskiy.apps.magemediation.source.Row
import net.pototskiy.apps.magemediation.source.Sheet

class CsvRow(
    private val _row: Int,
    private val _data: Array<String>,
    private val _sheet: CsvSheet
) : Row {
    override fun getOrEmptyCell(column: Int): Cell = get(column)
        ?: CsvCell(CellAddress(_row, column), "", this)

    override val sheet: Sheet
        get() = _sheet
    override val rowNum: Int
        get() = _row

    override fun get(column: Int): CsvCell? =
        if (column < _data.size) {
            CsvCell(
                CellAddress(
                    _row,
                    column
                ), _data[column], this
            )
        } else {
            null
        }

    override fun countCell(): Int = _data.size

    override fun iterator(): Iterator<CsvCell?> =
        CsvCellIterator(this)
}
