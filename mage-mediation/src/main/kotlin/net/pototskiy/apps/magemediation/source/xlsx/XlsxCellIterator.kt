package net.pototskiy.apps.magemediation.source.xlsx

import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow

class XlsxCellIterator(row: XSSFRow) : Iterator<XlsxCell> {
    private val iterator = row.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): XlsxCell = XlsxCell(iterator.next() as XSSFCell)
}