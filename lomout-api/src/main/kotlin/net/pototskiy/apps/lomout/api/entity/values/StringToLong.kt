package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.STRING
import java.text.NumberFormat
import java.text.ParseException
import java.text.ParsePosition
import java.util.*

/**
 * Convert String to Long according to the locale
 *
 * @receiver String
 * @param locale Locale
 * @return Long
 * @throws java.text.ParseException
 */
fun String.stringToLong(locale: Locale, groupingUsed: Boolean): Long {
    val format = NumberFormat.getIntegerInstance(locale).apply {
        isParseIntegerOnly = true
        isGroupingUsed = groupingUsed
    }
    val position = ParsePosition(0)
    val value = format.parse(this.trim(), position)
        ?: throw ParseException("String cannot be parsed to long.", position.index)
    if (position.index != this.trim().length) {
        throw ParseException("String contains extra characters.", position.index)
    }
    return value.toLong()
}

/**
 * Convert [PersistentString] to Long according to the locale
 *
 * @receiver PersistentString
 * @param locale The locale
 * @param groupingUsed Grouping is used
 * @return Long
 */
@Suppress("unused")
fun STRING.stringToLong(locale: Locale, groupingUsed: Boolean): Long =
    this.value.stringToLong(locale, groupingUsed)

/**
 * Convert Long to String according to the locale
 *
 * @receiver Long
 * @param locale Locale
 * @return String
 */
fun Long.longToString(locale: Locale): String {
    val format = NumberFormat.getIntegerInstance(locale)
    return format.format(this)
}

/**
 * Convert [PersistentLong] to String according to the locale
 *
 * @receiver PersistentLong
 * @param locale The locale
 * @return String
 */
@Suppress("unused")
fun LONG.longToString(locale: Locale): String = this.value.longToString(locale)
