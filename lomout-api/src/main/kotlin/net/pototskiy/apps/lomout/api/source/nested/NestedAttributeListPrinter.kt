package net.pototskiy.apps.lomout.api.source.nested

import java.io.StringWriter

/**
 * Nested attribute printer. Print map of attributes into string
 *
 * @constructor
 * @param quote Char? The name-value pair quote, null — no quote
 * @param delimiter Char The delimiter between name-value pairs
 * @param valueQuote Char? The value quote, null — no quote
 * @param valueDelimiter Char The delimiter between name and value
 */
class NestedAttributeListPrinter(
    quote: Char?,
    delimiter: Char,
    valueQuote: Char?,
    valueDelimiter: Char
) : NestedAttributeListFormat(quote, delimiter, valueQuote, valueDelimiter) {

    /**
     * Convert map of attributes(name→value) to string
     *
     * @param value Map<String, String> The attributes map
     * @return String
     */
    fun print(value: Map<String, String>): String {
        val values = value.map { (attr, value) ->
            StringWriter().use { writer ->
                getNameValueFormat().print(writer).use { it.printRecord(attr, value) }
                writer.toString()
            }
        }
        return StringWriter().use { writer ->
            getAttrFormat().print(writer).use { printer ->
                printer.printRecord(values)
            }
            writer.toString()
        }
    }
}
