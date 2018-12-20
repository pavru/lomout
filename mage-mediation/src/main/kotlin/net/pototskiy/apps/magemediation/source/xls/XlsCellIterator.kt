package net.pototskiy.apps.magemediation.source.xls

import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow

class XlsCellIterator(row: HSSFRow) : Iterator<XlsCell> {
    private val iterator = row.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): XlsCell =
        XlsCell(iterator.next() as HSSFCell)
}