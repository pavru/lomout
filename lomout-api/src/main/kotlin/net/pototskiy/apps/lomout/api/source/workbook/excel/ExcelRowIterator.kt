package net.pototskiy.apps.lomout.api.source.workbook.excel

import org.apache.poi.ss.usermodel.Row

/**
 * Excel sheet row iterator
 *
 * @property iterator MutableIterator<(org.apache.poi.ss.usermodel.Row..org.apache.poi.ss.usermodel.Row?)>
 * @constructor
 */
class ExcelRowIterator(sheet: org.apache.poi.ss.usermodel.Sheet) : Iterator<ExcelRow> {
    private val iterator = sheet.iterator()

    /**
     * Check if row has next cell
     *
     * @return Boolean
     */
    override fun hasNext(): Boolean = iterator.hasNext()

    /**
     * Get next sheet row
     *
     * @return ExcelRow
     */
    override fun next(): ExcelRow =
        ExcelRow(iterator.next() as Row)
}
