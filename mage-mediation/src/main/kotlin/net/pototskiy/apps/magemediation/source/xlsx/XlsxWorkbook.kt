package net.pototskiy.apps.magemediation.source.xlsx

import net.pototskiy.apps.magemediation.source.Workbook
import net.pototskiy.apps.magemediation.source.WorkbookType
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class XlsxWorkbook(
    private val workbook: XSSFWorkbook
) : Workbook {
    override val name: String
        get() = workbook.getFileName()
    override val type: WorkbookType
        get() = WorkbookType.EXCEL

    override fun get(sheet: String): XlsxSheet = XlsxSheet(workbook.getSheet(sheet))

    override fun get(sheet: Int): XlsxSheet = XlsxSheet(workbook.getSheetAt(sheet))
    override fun hasSheet(sheet: String): Boolean {
        return this.any { it.name == sheet }
    }

    override fun iterator(): Iterator<XlsxSheet> = XlsxSheetIterator(workbook)
}