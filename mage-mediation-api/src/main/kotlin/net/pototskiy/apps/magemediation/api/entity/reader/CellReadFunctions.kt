package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.sqlType
import net.pototskiy.apps.magemediation.api.entity.values.*
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.jetbrains.exposed.sql.DateColumnType
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

fun Cell.readeDateTime(
    attribute: Attribute<*>,
    locale: Locale
): DateTime? = when (this.cellType) {
    CellType.LONG -> DateTime(Date(this.longValue))
    CellType.DOUBLE -> DateTime(HSSFDateUtil.getJavaDate(this.doubleValue))
    CellType.BOOL -> null
    CellType.STRING -> if (attribute.valueType.sqlType() == DateColumnType::class) {
        this.stringValue.stringToDate(locale)
    } else {
        this.stringValue.stringToDateTime(locale)
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
    CellType.STRING -> DateTimeFormat.forPattern(pattern).parseDateTime(this.stringValue)
    CellType.BLANK -> null
}


fun Cell.readBoolean(locale: Locale): Boolean? = when (this.cellType) {
    CellType.LONG -> this.longValue != 0L
    CellType.DOUBLE -> this.doubleValue != 0.0
    CellType.BOOL -> this.booleanValue
    CellType.STRING -> this.stringValue.stringToBoolean(locale)
    CellType.BLANK -> null
}

fun Cell.readDouble(locale: Locale): Double? = when (this.cellType) {
    CellType.LONG -> this.longValue.toDouble()
    CellType.DOUBLE -> this.doubleValue
    CellType.BOOL -> if (this.booleanValue) 1.0 else 0.0
    CellType.STRING -> this.stringValue.stringToDouble(locale)
    CellType.BLANK -> null
}

fun Cell.readLong(locale: Locale): Long? = when (this.cellType) {
    CellType.LONG -> this.longValue
    CellType.DOUBLE -> this.doubleValue.doubleToLong()
    CellType.BOOL -> if (this.booleanValue) 1L else 0L
    CellType.STRING -> this.stringValue.stringToLong(locale)
    CellType.BLANK -> null
}

fun Cell.readString(locale: Locale): String? = when(this.cellType){
    CellType.LONG -> this.longValue.longToString(locale)
    CellType.DOUBLE -> this.doubleValue.doubleToString(locale)
    CellType.BOOL -> if (this.booleanValue) "1" else "0"
    CellType.STRING -> this.stringValue
    CellType.BLANK -> null
}
