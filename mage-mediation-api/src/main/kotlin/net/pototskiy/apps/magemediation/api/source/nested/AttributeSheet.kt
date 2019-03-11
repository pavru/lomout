package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.source.workbook.Row
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook

class AttributeSheet(
    private val backingWorkbook: AttributeWorkbook
) : Sheet {
    override fun insertRow(row: Int): Row = AttributeRow(
        row,
        when (row) {
            0 -> (workbook as AttributeWorkbook).cells[0]
            1 -> (workbook as AttributeWorkbook).cells[1]
            else -> throw SourceException("Attribute workbook has only 2 rows")
        },
        this
    )

    override val name: String
        get() = "default"
    override val workbook: Workbook
        get() = backingWorkbook

    override fun get(row: Int): Row = AttributeRow(
        row,
        when (row) {
            0 -> (workbook as AttributeWorkbook).cells[0]
            1 -> (workbook as AttributeWorkbook).cells[1]
            else -> throw SourceException("Attribute workbook has only 2 rows")
        },
        this
    )

    override fun iterator(): Iterator<Row> = AttributeRowIterator(this)
}
