package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.MessageBundle.message
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
        ?: throw ParseException(message("message.error.data.string.to_double_error"), position.index)
    if (position.index != this.trim().length) {
        throw ParseException(message("message.error.data.string.to_double_extra"), position.index)
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
