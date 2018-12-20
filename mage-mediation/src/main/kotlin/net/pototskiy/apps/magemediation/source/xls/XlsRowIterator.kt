package net.pototskiy.apps.magemediation.source.xls

import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet

class XlsRowIterator(sheet: HSSFSheet) : Iterator<XlsRow> {
    private val iterator = sheet.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): XlsRow =
        XlsRow(iterator.next() as HSSFRow)
}