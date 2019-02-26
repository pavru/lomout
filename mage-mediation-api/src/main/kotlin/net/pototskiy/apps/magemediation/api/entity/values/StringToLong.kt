package net.pototskiy.apps.magemediation.api.entity.values

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
fun String.stringToLong(locale: Locale): Long {
    val format = NumberFormat.getIntegerInstance(locale).apply {
        isParseIntegerOnly = true
    }
    val position = ParsePosition(0)
    val value = format.parse(this.trim(), position).toLong()
    if (position.index != this.trim().length) {
        throw ParseException("String contains extra characters", position.index)
    }
    return value
}

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
