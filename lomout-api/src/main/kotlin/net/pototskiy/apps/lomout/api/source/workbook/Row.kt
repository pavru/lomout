package net.pototskiy.apps.lomout.api.source.workbook

/**
 * Workbook row interface
 *
 * @property sheet Sheet
 * @property rowNum Int
 */
interface Row : Iterable<Cell?> {
    /**
     * Row sheet
     */
    val sheet: Sheet
    /**
     * Row number(index), zero based
     */
    val rowNum: Int

    /**
     * Row cells count
     *
     * @return Int
     */
    fun countCell(): Int

    /**
     * Get cell by index
     *
     * @param column Int The cell index(column), zero base
     * @return Cell?
     */
    operator fun get(column: Int): Cell?

    /**
     * Insert cell in row by index
     *
     * @param column Int The cell index(column), zero based
     * @return Cell The inserted cell
     */
    fun insertCell(column: Int): Cell

    /**
     * Get cell by index
     * Return cell from row or empty cell if it does not exist
     *
     * @param column Int The cell index(column), zero based
     * @return Cell
     */
    fun getOrEmptyCell(column: Int): Cell
}
