package net.pototskiy.apps.lomout.api.source.workbook.excel

import org.apache.poi.ss.usermodel.Sheet

/**
 * Workbook sheet iterator
 *
 * @property iterator MutableIterator<(org.apache.poi.ss.usermodel.Sheet..org.apache.poi.ss.usermodel.Sheet?)>
 * @constructor
 */
class ExcelSheetIterator(workbook: org.apache.poi.ss.usermodel.Workbook) : Iterator<ExcelSheet> {
    private val iterator = workbook.iterator()

    /**
     * Check if workbook has next sheet
     *
     * @return Boolean
     */
    override fun hasNext(): Boolean = iterator.hasNext()

    /**
     * Get next workbook sheet
     *
     * @return ExcelSheet
     */
    override fun next(): ExcelSheet =
        ExcelSheet(iterator.next() as Sheet)
}
