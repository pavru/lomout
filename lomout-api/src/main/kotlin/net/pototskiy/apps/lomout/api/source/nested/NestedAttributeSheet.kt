package net.pototskiy.apps.lomout.api.source.nested

import net.pototskiy.apps.lomout.api.AppWorkbookException
import net.pototskiy.apps.lomout.api.CSV_SHEET_NAME
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook

/**
 * Attribute workbook sheet
 *
 * @property backingWorkbook NestedAttributeWorkbook The sheet workbook
 * @property name String
 * @property workbook Workbook
 * @constructor
 */
class NestedAttributeSheet(
    private val backingWorkbook: NestedAttributeWorkbook
) : Sheet {
    /**
     * Insert row into sheet by the index
     *
     * @param row Int, The row index, zero based, only 0 and 1 are allowed
     * @return Row The inserted row
     */
    override fun insertRow(row: Int): Row = NestedAttributeRow(
        row,
        when (row) {
            0 -> (workbook as NestedAttributeWorkbook).cells[0]
            1 -> (workbook as NestedAttributeWorkbook).cells[1]
            else -> throw AppWorkbookException("Attribute workbook has only 2 rows")
        },
        this
    )

    /**
     * Sheet name, always is [CSV_SHEET_NAME]
     */
    override val name: String
        get() = CSV_SHEET_NAME
    /**
     * Sheet workbook
     */
    override val workbook: Workbook
        get() = backingWorkbook

    /**
     * Get row by the index
     *
     * @param row Int The row index, only 0 and 1 is allowed
     * @return Row
     */
    override fun get(row: Int): Row = NestedAttributeRow(
        row,
        when (row) {
            0 -> (workbook as NestedAttributeWorkbook).cells[0]
            1 -> (workbook as NestedAttributeWorkbook).cells[1]
            else -> throw AppWorkbookException("Attribute workbook has only 2 rows")
        },
        this
    )

    /**
     * Get workbook sheet iterator
     *
     * @return Iterator<Row>
     */
    override fun iterator(): Iterator<Row> = NestedAttributeRowIterator(this)
}
