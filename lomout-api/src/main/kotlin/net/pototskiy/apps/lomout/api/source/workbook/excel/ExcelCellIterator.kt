package net.pototskiy.apps.lomout.api.source.workbook.excel

import org.apache.poi.ss.usermodel.Cell

/**
 * Excel row cell iterator
 *
 * @constructor
 */
class ExcelCellIterator(row: org.apache.poi.ss.usermodel.Row) : Iterator<ExcelCell> {
    private val iterator = row.iterator()

    /**
     * Check if sheet has a next row
     *
     * @return Boolean
     */
    override fun hasNext(): Boolean = iterator.hasNext()

    /**
     * Get next sheet row
     *
     * @return ExcelCell
     */
    override fun next(): ExcelCell =
        ExcelCell(iterator.next() as Cell)
}
