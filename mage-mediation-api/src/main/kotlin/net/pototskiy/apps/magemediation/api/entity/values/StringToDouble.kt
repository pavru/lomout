package net.pototskiy.apps.magemediation.api.entity.values

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
fun String.stringToDouble(locale: Locale): Double {
    val format = NumberFormat.getNumberInstance(locale)
    val potision  = ParsePosition(0)
    val value = format.parse(this.trim(), potision).toDouble()
    if (potision.index != this.trim().length) {
        throw ParseException("String contains extra characters", potision.index)
    }
    return value
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

