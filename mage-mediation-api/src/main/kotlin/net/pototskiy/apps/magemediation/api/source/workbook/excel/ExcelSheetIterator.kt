package net.pototskiy.apps.magemediation.api.source.workbook.excel

import org.apache.poi.ss.usermodel.Sheet

class ExcelSheetIterator(workbook: org.apache.poi.ss.usermodel.Workbook): Iterator<ExcelSheet> {
    private val iterator = workbook.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): ExcelSheet =
        ExcelSheet(iterator.next() as Sheet)
}
