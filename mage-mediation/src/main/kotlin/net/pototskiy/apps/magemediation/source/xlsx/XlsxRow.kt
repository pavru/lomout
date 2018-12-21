package net.pototskiy.apps.magemediation.source.xlsx

import net.pototskiy.apps.magemediation.source.Row
import net.pototskiy.apps.magemediation.source.Sheet
import org.apache.poi.xssf.usermodel.XSSFRow

class XlsxRow(private val row: XSSFRow): Row {
    override val sheet: Sheet
        get() = XlsxSheet(row.sheet)
    override val rowNum: Int
        get() = row.rowNum

    override fun get(column: Int): XlsxCell = XlsxCell(row.getCell(column))
    override fun countCell(): Int = row.lastCellNum.toInt()
    override fun iterator(): Iterator<XlsxCell> = XlsxCellIterator(row)
}