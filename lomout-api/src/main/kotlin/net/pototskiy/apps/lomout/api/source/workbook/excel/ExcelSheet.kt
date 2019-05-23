package net.pototskiy.apps.lomout.api.source.workbook.excel

import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook

/**
 * Excel file sheet
 *
 * @property sheet Sheet
 * @property name String
 * @property workbook Workbook
 * @constructor
 */
class ExcelSheet(private val sheet: org.apache.poi.ss.usermodel.Sheet) : Sheet {
    /**
     * Insert new row to sheet
     *
     * @param row Int The row number to insert
     * @return Row Inserted row
     */
    override fun insertRow(row: Int): Row {
        return ExcelRow(sheet.createRow(row))
    }

    override val name: String
        get() = sheet.sheetName
    override val workbook: Workbook
        get() = ExcelWorkbook(sheet.workbook)

    /**
     * Get sheet row by the index
     *
     * @param row Int The row number (index)
     * @return ExcelRow? The row or null
     */
    override operator fun get(row: Int): ExcelRow? =
        sheet.getRow(row)?.let { ExcelRow(it) }

    /**
     * Get row iterator
     *
     * @return Iterator<ExcelRow>
     */
    override fun iterator(): Iterator<ExcelRow> =
        ExcelRowIterator(sheet)
}
