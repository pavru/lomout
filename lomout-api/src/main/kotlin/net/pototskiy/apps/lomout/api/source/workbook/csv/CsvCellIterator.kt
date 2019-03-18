package net.pototskiy.apps.lomout.api.source.workbook.csv

class CsvCellIterator(private val row: CsvRow) : Iterator<CsvCell?> {

    private var index = 0

    override fun hasNext(): Boolean = index < row.countCell()

    override fun next(): CsvCell? {
        return row[index++]
    }
}
