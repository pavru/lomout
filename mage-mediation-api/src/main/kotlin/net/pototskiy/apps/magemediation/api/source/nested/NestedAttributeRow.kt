package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.Row
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet

class NestedAttributeRow(
    private val backingRow: Int,
    private val backingData: MutableList<NestedAttributeCell>,
    private val backingSheet: NestedAttributeSheet
) : Row {
    override fun insertCell(column: Int): Cell {
        val cell = NestedAttributeCell(CellAddress(backingRow, column), "", this)
        backingData.add(column, cell)
        return cell
    }

    override fun getOrEmptyCell(column: Int): Cell = get(column)

    override val sheet: Sheet
        get() = backingSheet
    override val rowNum: Int
        get() = backingRow

    override fun countCell(): Int = backingData.count()

    override operator fun get(column: Int): Cell = backingData[column]

    override fun iterator(): Iterator<Cell?> = AttributeCellIterator(this)
}
