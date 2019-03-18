package net.pototskiy.apps.lomout.api.source.nested

import net.pototskiy.apps.lomout.api.AppWorkbookException
import net.pototskiy.apps.lomout.api.CSV_SHEET_NAME
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook

class NestedAttributeSheet(
    private val backingWorkbook: NestedAttributeWorkbook
) : Sheet {
    override fun insertRow(row: Int): Row = NestedAttributeRow(
        row,
        when (row) {
            0 -> (workbook as NestedAttributeWorkbook).cells[0]
            1 -> (workbook as NestedAttributeWorkbook).cells[1]
            else -> throw AppWorkbookException("Attribute workbook has only 2 rows")
        },
        this
    )

    override val name: String
        get() = CSV_SHEET_NAME
    override val workbook: Workbook
        get() = backingWorkbook

    override fun get(row: Int): Row = NestedAttributeRow(
        row,
        when (row) {
            0 -> (workbook as NestedAttributeWorkbook).cells[0]
            1 -> (workbook as NestedAttributeWorkbook).cells[1]
            else -> throw AppWorkbookException("Attribute workbook has only 2 rows")
        },
        this
    )

    override fun iterator(): Iterator<Row> = NestedAttributeRowIterator(this)
}
