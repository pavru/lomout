package net.pototskiy.apps.lomout.api.source.workbook.csv

/**
 * CSV row cell iterator
 *
 * @property row CsvRow
 * @property index Int
 * @constructor
 */
class CsvCellIterator(private val row: CsvRow) : Iterator<CsvCell?> {

    private var index = 0

    /**
     * Test row has nex cell
     *
     * @return Boolean
     */
    override fun hasNext(): Boolean = index < row.countCell()

    /**
     * Get next row cell
     * @return CsvCell?
     */
    override fun next(): CsvCell? {
        return row[index++]
    }
}
