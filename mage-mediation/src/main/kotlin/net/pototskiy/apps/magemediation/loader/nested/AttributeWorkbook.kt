package net.pototskiy.apps.magemediation.loader.nested

import net.pototskiy.apps.magemediation.source.Sheet
import net.pototskiy.apps.magemediation.source.Workbook
import net.pototskiy.apps.magemediation.source.WorkbookType

class AttributeWorkbook(
    val parser: AttributeListParser,
    private val _attribute_name: String
) : Workbook {
    override val name: String
        get() = "workbook_for_$_attribute_name"
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
