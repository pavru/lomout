package net.pototskiy.apps.magemediation.source.xls

import net.pototskiy.apps.magemediation.source.Workbook
import net.pototskiy.apps.magemediation.source.WorkbookType
import org.apache.poi.hssf.usermodel.HSSFWorkbook

class XlsWorkbook(
    private val workbook: HSSFWorkbook
) : Workbook {
    override val name: String
        get() = workbook.getFileName()
    override val type: WorkbookType
        get() = WorkbookType.EXCEL

    override fun get(sheet: String): XlsSheet =
        XlsSheet(workbook.getSheet(sheet))

    override fun get(sheet: Int): XlsSheet =
        XlsSheet(workbook.getSheetAt(sheet))
    override fun hasSheet(sheet: String): Boolean {
        return this.any { it.name == sheet }
    }

    override fun iterator(): Iterator<XlsSheet> =
        XlsSheetIterator(workbook)
}