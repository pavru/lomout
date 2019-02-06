package net.pototskiy.apps.magemediation.source.csv

class CsvCellIterator(private val row: CsvRow) : Iterator<CsvCell?> {

    private var index = 0

    override fun hasNext(): Boolean = index < row.countCell()

    override fun next(): CsvCell? {
        return row[index++]
    }
}
