package net.pototskiy.apps.lomout.api.source.nested

/**
 * Attribute workbook row iterator
 *
 * @property sheet NestedAttributeSheet
 * @property index Int
 * @constructor
 */
class NestedAttributeRowIterator(private val sheet: NestedAttributeSheet) : Iterator<NestedAttributeRow> {
    private var index = 0
    /**
     * Test if sheet has next row
     * @return Boolean
     */
    override fun hasNext(): Boolean = index < 1

    /**
     * Get next sheet row
     *
     * @return NestedAttributeRow
     */
    override fun next(): NestedAttributeRow = sheet[index++] as NestedAttributeRow
}
