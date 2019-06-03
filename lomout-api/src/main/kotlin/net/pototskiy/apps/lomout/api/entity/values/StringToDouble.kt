package net.pototskiy.apps.lomout.api.entity.values

import java.text.NumberFormat
import java.text.ParseException
import java.text.ParsePosition
import java.util.*

/**
 * Convert String to Double according to the locale
 *
 * @receiver String
 * @param locale Locale
 * @return Double?
 * @throws java.text.ParseException
 */
fun String.stringToDouble(locale: Locale, groupingUsed: Boolean): Double {
    val format = NumberFormat.getInstance(locale).apply {
        isGroupingUsed = groupingUsed
    }
    val position = ParsePosition(0)
    val value = format.parse(this.trim(), position)
        ?: throw ParseException("String cannot be parsed to double.", position.index)
    if (position.index != this.trim().length) {
        throw ParseException("String contains extra characters.", position.index)
    }
    return value.toDouble()
}

/**
 * Convert Double to String according to the locale
 *
 * @receiver Double
 * @param locale Locale
 * @return String
 */
fun Double.doubleToString(locale: Locale): String {
    val format = NumberFormat.getNumberInstance(locale)
    return format.format(this)
}
