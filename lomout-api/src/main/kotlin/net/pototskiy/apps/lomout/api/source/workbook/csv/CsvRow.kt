package net.pototskiy.apps.lomout.api.source.workbook.csv

import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import org.apache.commons.csv.CSVRecord

class CsvRow(
    private val backingRow: Int,
    data: CSVRecord?,
    private val backingSheet: CsvSheet
) : Row {
    private val cells: MutableList<CsvCell?> = mutableListOf()

    init {
        data?.forEachIndexed { c, value ->
            cells.add(CsvCell(CellAddress(backingRow, c), value, this))
        }
    }

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
        if (column < cells.size) {
            cells[column]
        } else {
            null
        }

    override fun insertCell(column: Int): Cell {
        checkThatItIsCsvOutputWorkbook(backingSheet.workbook as CsvWorkbook)
        val newCell = CsvCell(CellAddress(backingRow, column), "", this)
        cells.add(column, newCell)
        cells.forEachIndexed { c, cell ->
            cell?.address?.column = c
        }
        return newCell
    }

    override fun countCell(): Int = cells.size

    override fun iterator(): Iterator<CsvCell?> =
        CsvCellIterator(this)
}
