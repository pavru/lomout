package net.pototskiy.apps.magemediation.source.excel

class ExcelSheetIterator(workbook: org.apache.poi.ss.usermodel.Workbook): Iterator<ExcelSheet> {
    private val iterator = workbook.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): ExcelSheet = ExcelSheet(iterator.next() as org.apache.poi.ss.usermodel.Sheet)
}