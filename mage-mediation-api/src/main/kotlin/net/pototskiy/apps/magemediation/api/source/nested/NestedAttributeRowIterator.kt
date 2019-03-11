package net.pototskiy.apps.magemediation.api.source.nested

class NestedAttributeRowIterator(private val sheet: NestedAttributeSheet) : Iterator<NestedAttributeRow> {
    private var index = 0
    override fun hasNext(): Boolean = index < 1

    override fun next(): NestedAttributeRow = sheet[index++] as NestedAttributeRow
}
