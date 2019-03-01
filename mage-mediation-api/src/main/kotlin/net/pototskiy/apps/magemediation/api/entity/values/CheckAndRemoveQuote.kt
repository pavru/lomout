package net.pototskiy.apps.magemediation.api.entity.values

import net.pototskiy.apps.magemediation.api.plugable.PluginException

fun List<String>.checkAndRemoveQuote(quote: String?): List<String> {
    return quote?.takeIf { quote.isNotBlank() }?.let { notNullQuote ->
        this.map {
            val v = it.trim()
            if (!v.endsWith(notNullQuote) || !v.startsWith(notNullQuote)) {
                throw PluginException("$this should be quoted with $notNullQuote")
            }
            v.trim(notNullQuote[0])
        }
    } ?: this
}
