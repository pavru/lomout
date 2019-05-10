package net.pototskiy.apps.lomout.api.source.nested

import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet

/**
 * Attribute workbook row
 *
 * @property backingRow Int
 * @property backingData MutableList<NestedAttributeCell>
 * @property backingSheet NestedAttributeSheet
 * @property sheet Sheet
 * @property rowNum Int
 * @constructor
 * @param backingRow Int The row number(index)
 * @param backingData MutableList<NestedAttributeCell> Row cells
 * @param backingSheet NestedAttributeSheet The row sheet
 */
class NestedAttributeRow(
    private val backingRow: Int,
    private val backingData: MutableList<NestedAttributeCell>,
    private val backingSheet: NestedAttributeSheet
) : Row {
    /**
     * Insert cell into row, by index
     *
     * @param column Int The row index, zero based
     * @return Cell
     */
    override fun insertCell(column: Int): Cell {
        val cell = NestedAttributeCell(CellAddress(backingRow, column), "", this)
        backingData.add(column, cell)
        return cell
    }

    /**
     * Get cell by index, return empty cell it does exist in row
     *
     * @param column Int The cell index(column)
     * @return Cell
     */
    override fun getOrEmptyCell(column: Int): Cell = get(column)

    /**
     * Row sheet
     */
    override val sheet: Sheet
        get() = backingSheet
    /**
     * Row number(index)
     */
    override val rowNum: Int
        get() = backingRow

    /**
     * Get row cells count
     *
     * @return Int
     */
    override fun countCell(): Int = backingData.count()

    /**
     * Get row cell by index
     *
     * @param column Int The cell index(column), zero based
     * @return Cell
     */
    override operator fun get(column: Int): Cell = backingData[column]

    /**
     * Get cells iterator
     *
     * @return Iterator<Cell?>
     */
    override fun iterator(): Iterator<Cell?> = AttributeCellIterator(this)
}
