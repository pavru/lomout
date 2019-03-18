package net.pototskiy.apps.lomout.api.source.workbook.excel

import org.apache.poi.ss.usermodel.Row

class ExcelRowIterator(sheet: org.apache.poi.ss.usermodel.Sheet) : Iterator<ExcelRow> {
    private val iterator = sheet.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): ExcelRow =
        ExcelRow(iterator.next() as Row)
}
