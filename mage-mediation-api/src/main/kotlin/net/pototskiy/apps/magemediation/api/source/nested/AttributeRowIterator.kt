package net.pototskiy.apps.magemediation.api.source.nested

class AttributeRowIterator(private val sheet: AttributeSheet) : Iterator<AttributeRow> {
    private var index = 0
    override fun hasNext(): Boolean = index < 2

    override fun next(): AttributeRow = sheet[index++] as AttributeRow
}
