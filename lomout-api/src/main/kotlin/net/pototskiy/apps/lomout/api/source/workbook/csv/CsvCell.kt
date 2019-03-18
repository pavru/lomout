package net.pototskiy.apps.lomout.api.source.workbook.csv

import net.pototskiy.apps.lomout.api.entity.values.datetimeToString
import net.pototskiy.apps.lomout.api.entity.values.doubleToString
import net.pototskiy.apps.lomout.api.entity.values.longToString
import net.pototskiy.apps.lomout.api.entity.values.stringToBoolean
import net.pototskiy.apps.lomout.api.entity.values.stringToDouble
import net.pototskiy.apps.lomout.api.entity.values.stringToLong
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Row
import org.joda.time.DateTime
import java.text.ParseException

class CsvCell(
    private val backingAddress: CellAddress,
    private var backingValue: String,
    private val backingRow: CsvRow
) : Cell {
    override val address: CellAddress
        get() = backingAddress
    override val cellType: CellType
        get() = backingValue.recognize()
    override val booleanValue: Boolean
        get() = backingValue.trim().stringToBoolean(workbookLocale)
    override val longValue: Long
        get() = backingValue.stringToLong(workbookLocale)
    override val doubleValue: Double
        get() = backingValue.stringToDouble(workbookLocale)
    override val stringValue: String
        get() = backingValue

    private val workbookLocale = (backingRow.sheet.workbook as CsvWorkbook).workbookLocale

    override fun asString(): String {
        return backingValue
    }

    override fun setCellValue(value: String) {
        backingValue = value
    }

    override fun setCellValue(value: Boolean) {
        backingValue = if (value) "1" else "0"
    }

    override fun setCellValue(value: Long) {
        backingValue = value.longToString(workbookLocale)
    }

    override fun setCellValue(value: Double) {
        backingValue = value.doubleToString(workbookLocale)
    }

    override fun setCellValue(value: DateTime) {
        backingValue = value.datetimeToString(workbookLocale)
    }

    override val row: Row
        get() = backingRow

    private fun String.recognize(): CellType {
        return when {
            tryLong() -> CellType.LONG
            tryDouble() -> CellType.DOUBLE
            tryBoolean() -> CellType.BOOL
            this.isNotEmpty() -> CellType.STRING
            else -> CellType.BLANK
        }
    }

    private fun String.tryBoolean(): Boolean {
        return try {
            this.toLowerCase().trim().stringToBoolean(workbookLocale)
            true
        } catch (e: ParseException) {
            false
        }
    }

    private fun String.tryDouble(): Boolean {
        return try {
            this.stringToDouble(workbookLocale)
            true
        } catch (e: ParseException) {
            false
        }
    }

    private fun String.tryLong(): Boolean {
        return try {
            this.stringToLong(workbookLocale)
            true
        } catch (e: ParseException) {
            false
        }
    }
}
