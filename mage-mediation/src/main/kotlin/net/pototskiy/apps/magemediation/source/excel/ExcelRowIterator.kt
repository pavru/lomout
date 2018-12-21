package net.pototskiy.apps.magemediation.source.excel

class ExcelRowIterator(sheet: org.apache.poi.ss.usermodel.Sheet) : Iterator<ExcelRow> {
    private val iterator = sheet.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): ExcelRow = ExcelRow(iterator.next() as org.apache.poi.ss.usermodel.Row)
}