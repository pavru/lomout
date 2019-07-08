package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.MessageBundle.message
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
        ?: throw ParseException(message("message.error.data.string.to_long_error"), position.index)
    if (position.index != this.trim().length) {
        throw ParseException(message("message.error.data.string.to_long_extra"), position.index)
    }
    return value.toLong()
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
