package net.pototskiy.apps.magemediation.api.source.workbook.csv

import net.pototskiy.apps.magemediation.api.NOT_IMPLEMENTED
import net.pototskiy.apps.magemediation.api.entity.values.stringToBoolean
import net.pototskiy.apps.magemediation.api.entity.values.stringToDouble
import net.pototskiy.apps.magemediation.api.entity.values.stringToLong
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.Row
import org.joda.time.DateTime
import java.text.ParseException

class CsvCell(
    private val backingAddress: CellAddress,
    private val backingValue: String,
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
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Boolean) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Long) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Double) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: DateTime) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override val row: Row
        get() = backingRow

    private fun String.recognize(): CellType {
        return when {
            tryLong() -> CellType.LONG
            tryDouble() -> CellType.DOUBLE
            tryBoolean() -> CellType.BOOL
            this.isNotBlank() -> CellType.STRING
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
