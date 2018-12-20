package net.pototskiy.apps.magemediation.source.xls

import net.pototskiy.apps.magemediation.source.Row
import net.pototskiy.apps.magemediation.source.Sheet
import org.apache.poi.hssf.usermodel.HSSFRow

class XlsRow(private val row: HSSFRow) : Row {
    override val sheet: Sheet
        get() = XlsSheet(row.sheet)
    override val rowNum: Int
        get() = row.rowNum

    override fun get(column: Int): XlsCell =
        XlsCell(row.getCell(column))
    override fun countCell() = row.lastCellNum.toInt()
    override fun iterator(): Iterator<XlsCell> =
        XlsCellIterator(row)
}