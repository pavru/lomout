package net.pototskiy.apps.magemediation.api.source.nested

import java.io.StringWriter

class NestedAttributeListPrinter(
    quote: String?,
    delimiter: String,
    valueQuote: String?,
    valueDelimiter: String
) : NestedAttributeListFormat(quote, delimiter, valueQuote, valueDelimiter) {

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
