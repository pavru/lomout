package net.pototskiy.apps.lomout.api.source.nested

/**
 * Attribute workbook sheet iterator
 *
 * @property workbook NestedAttributeWorkbook
 * @property index Int
 * @constructor
 */
class NestedAttributeSheetIterator(private val workbook: NestedAttributeWorkbook) : Iterator<NestedAttributeSheet> {
    private var index = 0

    /**
     * Test if workbook has a next sheet
     *
     * @return Boolean
     */
    override fun hasNext(): Boolean = index < 1

    /**
     * Get next workbook sheet
     *
     * @return NestedAttributeSheet
     */
    override fun next(): NestedAttributeSheet = workbook[index++] as NestedAttributeSheet
}
