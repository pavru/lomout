package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.Row
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet

class AttributeRow(
    private val backingRow: Int,
    private val backingData: Array<String>,
    private val backingSheet: AttributeSheet
) : Row {
    override fun getOrEmptyCell(column: Int): Cell = get(column)

    override val sheet: Sheet
        get() = backingSheet
    override val rowNum: Int
        get() = backingRow

    override fun countCell(): Int = backingData.count()

    override fun get(column: Int): Cell = AttributeCell(
        CellAddress(
            backingRow,
            column
        ), backingData[column], this)

    override fun iterator(): Iterator<Cell?> = AttributeCellIterator(this)
}
