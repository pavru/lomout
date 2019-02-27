package net.pototskiy.apps.magemediation.api.source.workbook.excel

import net.pototskiy.apps.magemediation.api.source.workbook.Sheet
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook

class ExcelSheet(private val sheet: org.apache.poi.ss.usermodel.Sheet) :
    Sheet {
    override val name: String
        get() = sheet.sheetName
    override val workbook: Workbook
        get() = ExcelWorkbook(sheet.workbook)

    override fun get(row: Int): ExcelRow? = sheet.getRow(row)?.let {
        ExcelRow(
            it
        )
    }

    override fun iterator(): Iterator<ExcelRow> =
        ExcelRowIterator(sheet)
}
