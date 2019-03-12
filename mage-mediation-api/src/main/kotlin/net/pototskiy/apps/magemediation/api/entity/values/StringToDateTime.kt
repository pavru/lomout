package net.pototskiy.apps.magemediation.api.entity.values

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

fun String.stringToDate(locale: Locale): DateTime {
    val format = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("S-", locale))
    return try {
        format.parseDateTime(this.trim())
    } catch (e: IllegalArgumentException) {
        throw SourceException(
            "String can not be converted to date with locale<${locale.displayLanguage}_${locale.displayCountry}>.",
            e
        )
    }
}

fun String.stringToDateTime(pattern: String): DateTime {
    val format = DateTimeFormat.forPattern(pattern)
    return try {
        format.parseDateTime(this.trim())
    } catch (e: IllegalArgumentException) {
        throw SourceException("String can not be converted to date with pattern<$pattern>.", e)
    }
}

fun String.stringToDateTime(locale: Locale): DateTime {
    val format = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", locale))
    return try {
        format.parseDateTime(this.trim())
    } catch (e: IllegalArgumentException) {
        throw SourceException(
            "String can not be converted to date-time with locale ${locale.displayLanguage}_${locale.displayCountry}.",
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
