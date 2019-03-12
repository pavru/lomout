package net.pototskiy.apps.magemediation.api.entity.values

import net.pototskiy.apps.magemediation.api.source.workbook.SourceException

fun List<String>.checkAndRemoveQuote(quote: Char?): List<String> {
    return quote?.let { notNullQuote ->
        this.map {
            val v = it.trim()
            if (!v.endsWith(notNullQuote) || !v.startsWith(notNullQuote)) {
                throw SourceException("$this should be quoted with $notNullQuote")
            }
            v.drop(1).dropLast(1)
        }
    } ?: this
}
