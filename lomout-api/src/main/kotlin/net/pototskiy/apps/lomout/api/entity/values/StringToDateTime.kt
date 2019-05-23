package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.PublicApi
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

/**
 * Convert string value to [DateTime] (only date part) according to locale
 *
 * @receiver The string to convert
 * @param locale The locale for conversion
 * @return Value
 * @throws AppDataException The string cannot be converted to [DateTime]
 */
fun String.stringToDate(locale: Locale): DateTime {
    val format = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("S-", locale))
    return try {
        format.parseDateTime(this.trim())
    } catch (e: IllegalArgumentException) {
        throw AppDataException(
            "String cannot be converted to date with the locale<$locale>.",
            e
        )
    }
}

/**
 * Convert string to [DateTime] according to pattern.
 *
 * @receiver String
 * @param pattern String
 * @return DateTime
 * @throws AppDataException The string cannot be converted to [DateTime]
 */
fun String.stringToDateTime(pattern: String): DateTime {
    val format = DateTimeFormat.forPattern(pattern)
    return try {
        format.parseDateTime(this.trim())
    } catch (e: IllegalArgumentException) {
        throw AppDataException("String cannot be converted to date with the pattern<$pattern>.", e)
    }
}

/**
 * Convert string to [DateTime] according to locale
 *
 * @receiver String The string to convert
 * @param locale Locale The locale for conversion
 * @return DateTime
 * @throws AppDataException The string can be converted
 */
fun String.stringToDateTime(locale: Locale): DateTime {
    val format = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", locale))
    return try {
        format.parseDateTime(this.trim())
    } catch (e: IllegalArgumentException) {
        throw AppDataException(
            "String cannot be converted to date-time with the locale<$locale>.",
            e
        )
    }
}

/**
 * Convert [DateTime] (date part) to string according to locale
 *
 * @receiver DateTime The value to convert
 * @param locale Locale The locale for conversion
 * @return String
 */
fun DateTime.dateToString(locale: Locale): String =
    this.toString(DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("S-", locale)))

/**
 * Convert [DateTime] to string according to locale
 *
 * @receiver DateTime The value to convert
 * @param locale Locale The locale for conversion
 * @return String
 */
@PublicApi
fun DateTime.datetimeToString(locale: Locale): String =
    this.toString(DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", locale)))

/**
 * Convert [DateTime] to string according to pattern
 *
 * @receiver DateTime The value to convert
 * @param pattern String The pattern for conversion
 * @return String
 */
fun DateTime.datetimeToString(pattern: String): String = this.toString(pattern)
