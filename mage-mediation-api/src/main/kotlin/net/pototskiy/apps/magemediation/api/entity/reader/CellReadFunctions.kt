package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.AppCellDataException
import net.pototskiy.apps.magemediation.api.AppDataException
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.DateType
import net.pototskiy.apps.magemediation.api.entity.isTypeOf
import net.pototskiy.apps.magemediation.api.entity.values.doubleToLong
import net.pototskiy.apps.magemediation.api.entity.values.doubleToString
import net.pototskiy.apps.magemediation.api.entity.values.longToString
import net.pototskiy.apps.magemediation.api.entity.values.stringToBoolean
import net.pototskiy.apps.magemediation.api.entity.values.stringToDate
import net.pototskiy.apps.magemediation.api.entity.values.stringToDateTime
import net.pototskiy.apps.magemediation.api.entity.values.stringToDouble
import net.pototskiy.apps.magemediation.api.entity.values.stringToLong
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.joda.time.DateTime
import java.util.*

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

fun Cell.readString(locale: Locale): String? = when (this.cellType) {
    CellType.LONG -> this.longValue.longToString(locale)
    CellType.DOUBLE -> this.doubleValue.doubleToString(locale)
    CellType.BOOL -> if (this.booleanValue) "1" else "0"
    CellType.STRING -> this.stringValue
    CellType.BLANK -> null
}
