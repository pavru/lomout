package net.pototskiy.apps.magemediation.loader.nested

class AttributeCellIterator(private val row: AttributeRow) : Iterator<AttributeCell> {
    private var index = 0
    override fun hasNext(): Boolean = index < row.countCell()

    override fun next(): AttributeCell = row[index++] as AttributeCell
}