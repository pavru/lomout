package net.pototskiy.apps.magemediation.loader.nested

import net.pototskiy.apps.magemediation.source.Row
import net.pototskiy.apps.magemediation.source.Sheet
import net.pototskiy.apps.magemediation.source.Workbook

class AttributeSheet(
    private val _workbook: AttributeWorkbook
) : Sheet {
    override val name: String
        get() = "default"
    override val workbook: Workbook
        get() = _workbook

    override fun get(row: Int): Row = AttributeRow(row, _workbook.parser[row], this)

    override fun iterator(): Iterator<Row> = AttributeRowIterator(this)
}