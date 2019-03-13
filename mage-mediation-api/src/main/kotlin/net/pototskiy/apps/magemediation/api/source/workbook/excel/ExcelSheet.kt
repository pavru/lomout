package net.pototskiy.apps.magemediation.api.source.workbook.excel

import net.pototskiy.apps.magemediation.api.source.workbook.Row
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook

class ExcelSheet(private val sheet: org.apache.poi.ss.usermodel.Sheet) :
    Sheet {
    override fun insertRow(row: Int): Row {
        return ExcelRow(sheet.createRow(row))
    }

    override val name: String
        get() = sheet.sheetName
    override val workbook: Workbook
        get() = ExcelWorkbook(sheet.workbook)

    override operator fun get(row: Int): ExcelRow? =
        sheet.getRow(row)?.let { ExcelRow(it) }

    override fun iterator(): Iterator<ExcelRow> =
        ExcelRowIterator(sheet)
}
