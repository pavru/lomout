package net.pototskiy.apps.magemediation.api.source.nested

class AttributeCellIterator(private val row: NestedAttributeRow) : Iterator<NestedAttributeCell> {
    private var index = 0
    override fun hasNext(): Boolean = index < row.countCell()

    override fun next(): NestedAttributeCell = row[index++] as NestedAttributeCell
}
