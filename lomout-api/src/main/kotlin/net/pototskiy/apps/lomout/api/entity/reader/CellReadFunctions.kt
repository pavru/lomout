package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppCellDataException
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.DateType
import net.pototskiy.apps.lomout.api.entity.isTypeOf
import net.pototskiy.apps.lomout.api.entity.values.doubleToLong
import net.pototskiy.apps.lomout.api.entity.values.doubleToString
import net.pototskiy.apps.lomout.api.entity.values.longToString
import net.pototskiy.apps.lomout.api.entity.values.stringToBoolean
import net.pototskiy.apps.lomout.api.entity.values.stringToDate
import net.pototskiy.apps.lomout.api.entity.values.stringToDateTime
import net.pototskiy.apps.lomout.api.entity.values.stringToDouble
import net.pototskiy.apps.lomout.api.entity.values.stringToLong
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.joda.time.DateTime
import java.util.*

/**
 * Read [DateTime] from cell, non-DateTime values are converted to [DateTime]
 *
 * @receiver Cell The cell to read
 * @param attribute Attribute<*> The attribute for which value is read
 * @param locale Locale The locale for converting
 * @return DateTime?
 * @throws AppCellDataException The string value can not be converted to [DateTime]
 */
@Suppress("ComplexMethod")
fun Cell.readeDateTime(
    attribute: Attribute<*>,
    locale: Locale
): DateTime? = when (this.cellType) {
    CellType.LONG -> DateTime(Date(this.longValue))
    CellType.DOUBLE -> DateTime(HSSFDateUtil.getJavaDate(this.doubleValue))
    CellType.BOOL -> null
    CellType.STRING -> try {
        if (attribute.valueType.isTypeOf<DateType>()) {
            this.stringValue.stringToDate(locale)
        } else {
            this.stringValue.stringToDateTime(locale)
        }
    } catch (e: AppDataException) {
        throw AppCellDataException(e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read [DateTime] value from cell, non-[DateTime] values are converted to [DateTime]
 *
 * @receiver Cell The cell to read
 * @param attribute Attribute<*> The attribute for which value is read
 * @param pattern String The date-time pattern for converting
 * @return DateTime?
 * @throws AppCellDataException The string value can not be converted to [DateTime]
 */
fun Cell.readeDateTime(
    @Suppress("UNUSED_PARAMETER") attribute: Attribute<*>,
    pattern: String
): DateTime? = when (this.cellType) {
    CellType.LONG -> DateTime(Date(this.longValue))
    CellType.DOUBLE -> DateTime(HSSFDateUtil.getJavaDate(this.doubleValue))
    CellType.BOOL -> null
    CellType.STRING -> try {
        this.stringValue.stringToDateTime(pattern)
    } catch (e: AppDataException) {
        throw AppCellDataException(e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read Boolean value from cell, non-Boolean values are converted to Boolean
 *
 * @receiver Cell The cell to read
 * @param locale Locale The locale for converting
 * @return Boolean?
 */
fun Cell.readBoolean(locale: Locale): Boolean? = when (this.cellType) {
    CellType.LONG -> this.longValue != 0L
    CellType.DOUBLE -> this.doubleValue != 0.0
    CellType.BOOL -> this.booleanValue
    CellType.STRING -> try {
        this.stringValue.stringToBoolean(locale)
    } catch (e: AppDataException) {
        throw AppCellDataException(e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read Double value from cell, non-Double values are converted to Double
 *
 * @receiver Cell The cell to read
 * @param locale Locale The locale for converting
 * @return Double?
 */
@Suppress("ComplexMethod")
fun Cell.readDouble(locale: Locale): Double? = when (this.cellType) {
    CellType.LONG -> this.longValue.toDouble()
    CellType.DOUBLE -> this.doubleValue
    CellType.BOOL -> if (this.booleanValue) 1.0 else 0.0
    CellType.STRING -> try {
        this.stringValue.stringToDouble(locale)
    } catch (e: AppDataException) {
        throw AppCellDataException(e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read Long value from cell, non-Long values are converted to Long
 *
 * @receiver Cell The cell to read
 * @param locale Locale The locale for converting
 * @return Long?
 */
@Suppress("ComplexMethod")
fun Cell.readLong(locale: Locale): Long? = when (this.cellType) {
    CellType.LONG -> this.longValue
    CellType.DOUBLE -> this.doubleValue.doubleToLong()
    CellType.BOOL -> if (this.booleanValue) 1L else 0L
    CellType.STRING -> try {
        this.stringValue.stringToLong(locale)
    } catch (e: AppDataException) {
        throw AppCellDataException(e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read string from cell, non-string values converted to string according to locale
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
