package net.pototskiy.apps.magemediation.loader.nested

import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellAddress
import net.pototskiy.apps.magemediation.source.Row
import net.pototskiy.apps.magemediation.source.Sheet

class AttributeRow(
    private val _row: Int,
    private val _data: Array<String>,
    private val _sheet: AttributeSheet
) : Row {
    override fun getOrEmptyCell(column: Int): Cell = get(column)

    override val sheet: Sheet
        get() = _sheet
    override val rowNum: Int
        get() = _row

    override fun countCell(): Int = _data.count()

    override fun get(column: Int): Cell = AttributeCell(CellAddress(_row, column), _data[column], this)

    override fun iterator(): Iterator<Cell?> = AttributeCellIterator(this)
}
