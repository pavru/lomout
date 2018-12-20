package net.pototskiy.apps.magemediation.source.csv

class CsvRowIterator(private val sheet: CsvSheet) : Iterator<CsvRow> {
    private val iterator = sheet.parser.iterator()
    private var index = 0

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): CsvRow {
        val v = iterator.next()
        val data = mutableListOf<String>()
        for (s in v) data.add(s)
        return CsvRow(index, data.toTypedArray(), sheet).apply { index++ }
    }
}
