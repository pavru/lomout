package net.pototskiy.apps.lomout.api.source.workbook.csv

/**
 * CSV sheet row iterator
 *
 * @property sheet CsvSheet
 * @property iterator MutableIterator<(org.apache.commons.csv.CSVRecord..org.apache.commons.csv.CSVRecord?)>
 * @property index Int
 * @constructor
 */
class CsvRowIterator(private val sheet: CsvSheet) : Iterator<CsvRow> {
    init {
        checkThatItIsCsvInputWorkbook(sheet.workbook as CsvWorkbook)
    }

    private val iterator = sheet.parser.iterator()
    private var index = 0

    /**
     * Test sheet has next row
     * @return Boolean
     */
    override fun hasNext(): Boolean = iterator.hasNext()

    /**
     * Get sheet nex row
     *
     * @return CsvRow
     */
    override fun next(): CsvRow {
        val v = iterator.next()
        return CsvRow(index, v, sheet).apply { index++ }
    }
}
