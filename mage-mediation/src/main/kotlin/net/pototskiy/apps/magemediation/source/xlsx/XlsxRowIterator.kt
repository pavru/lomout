package net.pototskiy.apps.magemediation.source.xlsx

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet

class XlsxRowIterator(sheet: XSSFSheet) : Iterator<XlsxRow> {
    private val iterator = sheet.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): XlsxRow = XlsxRow(iterator.next() as XSSFRow)
}