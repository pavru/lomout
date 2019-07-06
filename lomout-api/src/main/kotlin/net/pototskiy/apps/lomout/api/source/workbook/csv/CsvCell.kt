package net.pototskiy.apps.lomout.api.source.workbook.csv

import net.pototskiy.apps.lomout.api.entity.values.dateToString
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
import java.text.ParseException
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * CSV workbook cell
 *
 * @property backingAddress CellAddress
 * @property backingValue String
 * @property backingRow CsvRow
 * @property address CellAddress
 * @property cellType CellType
 * @property booleanValue Boolean
 * @property longValue Long
 * @property doubleValue Double
 * @property stringValue String
 * @property workbookLocale Locale
 * @property row Row
 * @constructor
 */
class CsvCell(
    private val backingAddress: CellAddress,
    private var backingValue: String,
    private val backingRow: CsvRow
) : Cell {
    /**
     * In case of dell is number this flag indicates is it grouping.
     */
    private var isNumberGroupingUsed = false
    /**
     * Cell address
     */
    override val address: CellAddress
        get() = backingAddress
    /**
     * Cell type
     */
    override val cellType: CellType
        get() = backingValue.recognize()
    /**
     * Boolean value of cell
     */
    override val booleanValue: Boolean
        get() = backingValue.trim().stringToBoolean(workbookLocale)
    /**
     * Long value of cell
     */
    override val longValue: Long
        get() = backingValue.stringToLong(workbookLocale, isNumberGroupingUsed)
    /**
     * Double value of cell
     */
    override val doubleValue: Double
        get() = backingValue.stringToDouble(workbookLocale, isNumberGroupingUsed)
    /**
     * String value of cell
     */
    override val stringValue: String
        get() = backingValue

    private val workbookLocale = (backingRow.sheet.workbook as CsvWorkbook).workbookLocale

    /**
     * Get cell value as string
     *
     * @return String
     */
    override fun asString(): String {
        return backingValue
    }

    /**
     * Set string cell value
     *
     * @param value String
     */
    override fun setCellValue(value: String) {
        backingValue = value
    }

    /**
     * Set boolean cell value
     *
     * @param value Boolean
     */
    override fun setCellValue(value: Boolean) {
        backingValue = if (value) "1" else "0"
    }

    /**
     * Set long cell value
     *
     * @param value Long
     */
    override fun setCellValue(value: Long) {
        backingValue = value.longToString(workbookLocale)
    }

    /**
     * Set double cell value
     *
     * @param value Double
     */
    override fun setCellValue(value: Double) {
        backingValue = value.doubleToString(workbookLocale)
    }

    /**
     * Set datetime cell value
     *
     * @param value DateTime
     */
    override fun setCellValue(value: LocalDateTime) {
        backingValue = value.datetimeToString(workbookLocale)
    }

    /**
     * Set datetime cell value
     *
     * @param value DateTime
     */
    override fun setCellValue(value: LocalDate) {
        backingValue = value.dateToString(workbookLocale)
    }

    /**
     * Cell [Row]
     */
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
            this.stringToDouble(workbookLocale, false)
            isNumberGroupingUsed = false
            true
        } catch (e: ParseException) {
            false
        }
    }

    private fun String.tryLong(): Boolean {
        return try {
            this.stringToLong(workbookLocale, false)
            isNumberGroupingUsed = false
            true
        } catch (e: ParseException) {
            false
        }
    }
}
