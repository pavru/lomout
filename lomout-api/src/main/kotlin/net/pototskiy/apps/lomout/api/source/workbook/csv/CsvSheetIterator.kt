package net.pototskiy.apps.lomout.api.source.workbook.csv

/**
 * CSV workbook sheet iterator
 *
 * @property workbook CsvWorkbook
 * @property index Int
 * @constructor
 */
class CsvSheetIterator(private val workbook: CsvWorkbook) : Iterator<CsvSheet> {
    private var index = 0
    /**
     * Test if workbook has next sheet
     *
     * @return Boolean
     */
    override fun hasNext(): Boolean = index == 0

    /**
     * Get next workbook sheet
     *
     * @return CsvSheet
     */
    override fun next(): CsvSheet = CsvSheet(
        workbook
    ).also {
        workbook.sheet = it
        index++
    }
}
