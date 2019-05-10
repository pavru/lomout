package net.pototskiy.apps.lomout.api.source.nested

/**
 * Attribute workbook cell iterator
 *
 * @property row NestedAttributeRow
 * @property index Int
 * @constructor
 */
class AttributeCellIterator(private val row: NestedAttributeRow) : Iterator<NestedAttributeCell> {
    private var index = 0
    /**
     * Test if row has next cell
     *
     * @return Boolean
     */
    override fun hasNext(): Boolean = index < row.countCell()

    /**
     * Get next row cell
     *
     * @return NestedAttributeCell
     */
    override fun next(): NestedAttributeCell = row[index++] as NestedAttributeCell
}
