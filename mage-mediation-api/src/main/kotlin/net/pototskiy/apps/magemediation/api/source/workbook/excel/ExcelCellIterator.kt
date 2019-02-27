package net.pototskiy.apps.magemediation.api.source.workbook.excel

import org.apache.poi.ss.usermodel.Cell

class ExcelCellIterator(row: org.apache.poi.ss.usermodel.Row) : Iterator<ExcelCell> {
    private val iterator = row.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): ExcelCell =
        ExcelCell(iterator.next() as Cell)
}
