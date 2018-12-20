package net.pototskiy.apps.magemediation.loader.xls

import net.pototskiy.apps.magemediation.source.Sheet
import net.pototskiy.apps.magemediation.source.Workbook
import org.apache.poi.xssf.usermodel.XSSFSheet

class XlsxSheet(private val sheet: XSSFSheet) : Sheet {
    override val name: String
        get() = sheet.sheetName
    override val workbook: Workbook
        get() = XlsxWorkbook(sheet.workbook)

    override fun get(row: Int): XlsxRow = XlsxRow(sheet.getRow(row))

    override fun iterator(): Iterator<XlsxRow> = XlsxRowIterator(sheet)
}