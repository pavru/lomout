package net.pototskiy.apps.magemediation.source.xls

import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook

class XlsSheetIterator(workbook: HSSFWorkbook): Iterator<XlsSheet> {
    private val iterator = workbook.iterator()

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): XlsSheet =
        XlsSheet(iterator.next() as HSSFSheet)
}