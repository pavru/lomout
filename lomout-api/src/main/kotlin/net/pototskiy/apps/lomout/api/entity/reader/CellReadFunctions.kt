package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.entity.type.isTypeOf
import net.pototskiy.apps.lomout.api.entity.values.doubleToLong
import net.pototskiy.apps.lomout.api.entity.values.doubleToString
import net.pototskiy.apps.lomout.api.entity.values.longToString
import net.pototskiy.apps.lomout.api.entity.values.stringToBoolean
import net.pototskiy.apps.lomout.api.entity.values.stringToDate
import net.pototskiy.apps.lomout.api.entity.values.stringToDateTime
import net.pototskiy.apps.lomout.api.entity.values.stringToDouble
import net.pototskiy.apps.lomout.api.entity.values.stringToLong
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.joda.time.DateTime
import java.text.ParseException
import java.util.*

/**
 * Read [DateTime] from the cell. Convert non-DateTime values to [DateTime].
 *
 * @receiver Cell The cell to read
 * @param attribute The destination attribute
 * @param locale The locale for converting
 * @return Read value
 * @throws AppDataException The string value cannot be converted to [DateTime]
 */
@Suppress("ComplexMethod")
fun Cell.readeDateTime(
    attribute: Attribute<*>,
    locale: Locale
): DateTime? = when (this.cellType) {
    CellType.LONG -> DateTime(Date(this.longValue))
    CellType.DOUBLE -> DateTime(HSSFDateUtil.getJavaDate(this.doubleValue))
    CellType.BOOL -> null
    CellType.STRING ->
        try {
            if (attribute.type.isTypeOf<DATE>()) {
                this.stringValue.stringToDate(locale)
            } else {
                this.stringValue.stringToDateTime(locale)
            }
        } catch (e: AppDataException) {
            throw AppDataException(e.place + this + attribute, e.message, e)
        }
    CellType.BLANK -> null
}

/**
 * Read [DateTime] value from the cell. Convert non-[DateTime] values to [DateTime].
 *
 * @receiver The cell to read
 * @param attribute The destination attribute
 * @param pattern The date-time pattern for converting
 * @return Read value
 * @throws AppDataException The string value cannot be converted to [DateTime]
 */
fun Cell.readeDateTime(
    @Suppress("UNUSED_PARAMETER") attribute: Attribute<*>,
    pattern: String
): DateTime? = when (this.cellType) {
    CellType.LONG -> DateTime(Date(this.longValue))
    CellType.DOUBLE -> DateTime(HSSFDateUtil.getJavaDate(this.doubleValue))
    CellType.BOOL -> null
    CellType.STRING ->
        try {
            this.stringValue.stringToDateTime(pattern)
        } catch (e: AppDataException) {
            throw AppDataException(e.place + this + attribute, e.message, e)
        }
    CellType.BLANK -> null
}

/**
 * Read Boolean value from the cell. Convert non-Boolean values to Boolean.
 *
 * @receiver The cell to read
 * @param locale The locale for converting
 * @return The value
 */
fun Cell.readBoolean(locale: Locale): Boolean? = when (this.cellType) {
    CellType.LONG -> this.longValue != 0L
    CellType.DOUBLE -> this.doubleValue != 0.0
    CellType.BOOL -> this.booleanValue
    CellType.STRING -> try {
        this.stringValue.stringToBoolean(locale)
    } catch (e: AppDataException) {
        throw AppDataException(e.place + this, e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read Double value from the cell. Convert non-Double values to Double.
 *
 * @receiver The cell to read
 * @param locale The locale for converting
 * @return The value
 */
@Suppress("ComplexMethod")
fun Cell.readDouble(locale: Locale, groupingUsed: Boolean): Double? = when (this.cellType) {
    CellType.LONG -> this.longValue.toDouble()
    CellType.DOUBLE -> this.doubleValue
    CellType.BOOL -> if (this.booleanValue) 1.0 else 0.0
    CellType.STRING -> try {
        this.stringValue.stringToDouble(locale, groupingUsed)
    } catch (e: ParseException) {
        throw AppDataException(badPlace(this), e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read Long value from the cell. Convert non-Long values to Long when necessary.
 *
 * @receiver Cell The cell to read
 * @param locale Locale The locale for converting
 * @return Long?
 */
@Suppress("ComplexMethod")
fun Cell.readLong(locale: Locale, groupingUsed: Boolean): Long? = when (this.cellType) {
    CellType.LONG -> this.longValue
    CellType.DOUBLE -> this.doubleValue.doubleToLong()
    CellType.BOOL -> if (this.booleanValue) 1L else 0L
    CellType.STRING -> try {
        this.stringValue.stringToLong(locale, groupingUsed)
    } catch (e: ParseException) {
        throw AppDataException(badPlace(this), e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read string from the cell, non-string values converted to string according to locale
 *
 * @receiver Cell The cell to read
 * @param locale Locale The locale for converting
 * @return String?
 */
fun Cell.readString(locale: Locale): String? = when (this.cellType) {
    CellType.LONG -> this.longValue.longToString(locale)
    CellType.DOUBLE -> this.doubleValue.doubleToString(locale)
    CellType.BOOL -> if (this.booleanValue) "1" else "0"
    CellType.STRING -> this.stringValue
    CellType.BLANK -> null
}
