package net.pototskiy.apps.magemediation.source.excel

class ExcelCellIterator(row: org.apache.poi.ss.usermodel.Row) : Iterator<ExcelCell> {
    private val iterator = row.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): ExcelCell = ExcelCell(iterator.next() as org.apache.poi.ss.usermodel.Cell)
}