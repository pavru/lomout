package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.source.workbook.Row
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook

class AttributeSheet(
    private val backingWorkbook: AttributeWorkbook
) : Sheet {
    override val name: String
        get() = "default"
    override val workbook: Workbook
        get() = backingWorkbook

    override fun get(row: Int): Row = AttributeRow(row, backingWorkbook.parser[row], this)

    override fun iterator(): Iterator<Row> = AttributeRowIterator(this)
}
