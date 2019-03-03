package net.pototskiy.apps.magemediation.api.source.workbook.csv

import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.Row
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet

class CsvRow(
    private val backingRow: Int,
    private val backingData: Array<String>,
    private val backingSheet: CsvSheet
) : Row {
    override fun getOrEmptyCell(column: Int): Cell = get(column)
        ?: CsvCell(
            CellAddress(
                backingRow,
                column
            ), "", this
        )

    override val sheet: Sheet
        get() = backingSheet
    override val rowNum: Int
        get() = backingRow

    override fun get(column: Int): CsvCell? =
        if (column < backingData.size) {
            CsvCell(
                CellAddress(
                    backingRow,
                    column
                ), backingData[column], this
            )
        } else {
            null
        }

    override fun countCell(): Int = backingData.size

    override fun iterator(): Iterator<CsvCell?> =
        CsvCellIterator(this)
}
