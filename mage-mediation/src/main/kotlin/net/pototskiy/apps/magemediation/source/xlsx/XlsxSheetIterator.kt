package net.pototskiy.apps.magemediation.loader.xls

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class XlsxSheetIterator(workbook: XSSFWorkbook): Iterator<XlsxSheet> {
    private val iterator = workbook.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): XlsxSheet = XlsxSheet(iterator.next() as XSSFSheet)
}