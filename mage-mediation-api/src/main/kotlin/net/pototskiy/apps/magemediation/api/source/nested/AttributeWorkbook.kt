package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.source.workbook.Sheet
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookType

class AttributeWorkbook(
    val parser: AttributeListParser,
    private val backingAttributeName: String
) : Workbook {
    override val name: String
        get() = "workbook_for_$backingAttributeName"
    override val type: WorkbookType
        get() = WorkbookType.ATTRIBUTE

    override fun get(sheet: String): Sheet = AttributeSheet(this)
    override fun get(sheet: Int): Sheet = AttributeSheet(this)
    override fun hasSheet(sheet: String): Boolean = true
    override fun iterator(): Iterator<Sheet> = AttributeSheetIterator(this)
    override fun close() {
        // nothing to close workbook use string
    }
}
