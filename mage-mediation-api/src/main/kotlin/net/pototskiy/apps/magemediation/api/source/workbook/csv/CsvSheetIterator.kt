package net.pototskiy.apps.magemediation.api.source.workbook.csv

class CsvSheetIterator(private val workbook: CsvWorkbook): Iterator<CsvSheet> {
    private var index = 0
    override fun hasNext(): Boolean  = index == 0

    override fun next(): CsvSheet = CsvSheet(
        workbook
    ).apply { index++ }
}
