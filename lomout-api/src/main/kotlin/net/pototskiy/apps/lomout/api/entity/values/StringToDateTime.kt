@file:Suppress("TooManyFunctions")

package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.badData
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.entity.type.STRING
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
            badData(this),
            "String cannot be converted to date with the locale '$locale'.",
            e
        )
    }
}

/**
 * Convert [STRING] to DateTime (only date part) according to the locale
 *
 * @receiver PersistentString
 * @param locale The locale
 * @return DateTime
 */
@Suppress("unused")
fun STRING.stringToDate(locale: Locale): DateTime = this.value.stringToDate(locale)

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
        throw AppDataException(badData(this), "String cannot be converted to date with the pattern '$pattern'.", e)
    }
}

/**
 * Convert [STRING] to DateTime according to the pattern
 *
 * @receiver PersistentString
 * @param pattern The pattern
 * @return DateTime
 */
@Suppress("unused")
fun STRING.stringToDateTime(pattern: String): DateTime = this.value.stringToDateTime(pattern)

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
            badData(this),
            "String cannot be converted to date-time with the locale '$locale'.",
            e
        )
    }
}

/**
 * Convert [STRING] to DateTime according to the locale
 *
 * @receiver PersistentString
 * @param locale The locale
 * @return DateTime
 */
@Suppress("unused")
fun STRING.stringToDateTime(locale: Locale): DateTime = this.value.stringToDateTime(locale)

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
 * Convert [DATE] to String according to the locale
 *
 * @receiver PersistentDate
 * @param locale The locale
 * @return String
 */
@Suppress("unused")
fun DATE.dateToString(locale: Locale): String = this.value.dateToString(locale)

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
 * Convert [DATETIME] to String according to the locale
 *
 * @receiver PersistentDateTime
 * @param locale The locale
 * @return String
 */
@Suppress("unused")
fun DATETIME.datetimeToString(locale: Locale): String = this.value.datetimeToString(locale)

/**
 * Convert [DateTime] to string according to pattern
 *
 * @receiver DateTime The value to convert
 * @param pattern String The pattern for conversion
 * @return String
 */
fun DateTime.datetimeToString(pattern: String): String = this.toString(pattern)

/**
 * Convert [DATETIME] to String according to the pattern
 *
 * @receiver PersistentDateTime
 * @param pattern The pattern
 * @return String
 */
@Suppress("unused")
fun DATETIME.datetimeToString(pattern: String): String = this.value.datetimeToString(pattern)
