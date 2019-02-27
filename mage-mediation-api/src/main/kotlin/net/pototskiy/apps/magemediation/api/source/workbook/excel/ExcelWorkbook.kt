package net.pototskiy.apps.magemediation.api.source.workbook.excel

import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookType

class ExcelWorkbook<T : org.apache.poi.ss.usermodel.Workbook>(
    private val workbook: T
) : Workbook {
    override val name: String
        get() = workbook.getFileName()
    override val type: WorkbookType
        get() = WorkbookType.EXCEL

    override fun get(sheet: String): ExcelSheet =
        ExcelSheet(workbook.getSheet(sheet))

    override fun get(sheet: Int): ExcelSheet =
        ExcelSheet(workbook.getSheetAt(sheet))
    override fun hasSheet(sheet: String): Boolean {
        return this.any { it.name == sheet }
    }

    override fun iterator(): Iterator<ExcelSheet> =
        ExcelSheetIterator(workbook)
    override fun close() = workbook.close()
}
