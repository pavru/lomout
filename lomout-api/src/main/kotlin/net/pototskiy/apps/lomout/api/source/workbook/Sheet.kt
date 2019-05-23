package net.pototskiy.apps.lomout.api.source.workbook

/**
 * Workbook sheet interface
 *
 * @property name String
 * @property workbook Workbook
 */
interface Sheet : Iterable<Row> {
    /**
     * The sheet name
     */
    val name: String
    /**
     * Sheet workbook
     */
    val workbook: Workbook

    /**
     * Get row by the index
     *
     * @param row Int The row index, zero based
     * @return Row?
     */
    operator fun get(row: Int): Row?

    /**
     * Insert row into sheet by the index
     *
     * @param row Int The row index, zero based
     * @return Row
     */
    fun insertRow(row: Int): Row
}
