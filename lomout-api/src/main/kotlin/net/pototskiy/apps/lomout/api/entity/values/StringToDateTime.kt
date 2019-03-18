package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.PublicApi
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

fun String.stringToDate(locale: Locale): DateTime {
    val format = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("S-", locale))
    return try {
        format.parseDateTime(this.trim())
    } catch (e: IllegalArgumentException) {
        throw AppDataException(
            "String can not be converted to date with locale<$locale>.",
            e
        )
    }
}

fun String.stringToDateTime(pattern: String): DateTime {
    val format = DateTimeFormat.forPattern(pattern)
    return try {
        format.parseDateTime(this.trim())
    } catch (e: IllegalArgumentException) {
        throw AppDataException("String can not be converted to date with pattern<$pattern>.", e)
    }
}

fun String.stringToDateTime(locale: Locale): DateTime {
    val format = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", locale))
    return try {
        format.parseDateTime(this.trim())
    } catch (e: IllegalArgumentException) {
        throw AppDataException(
            "String can not be converted to date-time with locale<$locale>.",
            e
        )
    }
}

fun DateTime.dateToString(locale: Locale): String =
    this.toString(DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("S-", locale)))

@PublicApi
fun DateTime.datetimeToString(locale: Locale): String =
    this.toString(DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", locale)))

fun DateTime.datetimeToString(pattern: String): String = this.toString(pattern)
