package net.pototskiy.apps.magemediation.api.source.workbook.csv

class CsvRowIterator(private val sheet: CsvSheet) : Iterator<CsvRow> {
    init {
        checkThatItIsCsvInputWorkbook(sheet.workbook as CsvWorkbook)
    }
    private val iterator = sheet.parser.iterator()
    private var index = 0

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): CsvRow {
        val v = iterator.next()
        return CsvRow(index, v, sheet).apply { index++ }
    }
}
