package net.pototskiy.apps.magemediation.api

import java.util.*

@Suppress("TooGenericExceptionCaught")
fun String.createLocale(): Locale {
    return try {
        val (l, c) = this.split("_")
        Locale(l, c)
    } catch (e: Exception) {
        Locale.getDefault()
    }
}
