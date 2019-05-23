package net.pototskiy.apps.lomout.api

import java.util.*

@Suppress("TooGenericExceptionCaught")
/**
 * Convent string presentation of locale to Local object
 *
 * @receiver String The locale in form ll_CC where CC — country code,
 *  ll — language code
 * @return Locale
 */
fun String.createLocale(): Locale {
    return try {
        val (l, c) = this.split("_")
        Locale(l, c)
    } catch (e: Exception) {
        Locale.getDefault()
    }
}
