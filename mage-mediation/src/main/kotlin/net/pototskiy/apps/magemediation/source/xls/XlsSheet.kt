package net.pototskiy.apps.magemediation.source.xls

import net.pototskiy.apps.magemediation.source.Sheet
import net.pototskiy.apps.magemediation.source.Workbook
import org.apache.poi.hssf.usermodel.HSSFSheet

class XlsSheet(private val sheet: HSSFSheet) : Sheet {
    override val name: String
        get() = sheet.sheetName
    override val workbook: Workbook
        get() = XlsWorkbook(sheet.workbook)

    override fun get(row: Int): XlsRow =
        XlsRow(sheet.getRow(row))

    override fun iterator(): Iterator<XlsRow> =
        XlsRowIterator(sheet)
}