package net.pototskiy.apps.lomout.api.source.nested

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.QuoteMode

open class NestedAttributeListFormat(
    private val quote: Char?,
    private val delimiter: Char,
    private val valueQuote: Char?,
    private val valueDelimiter: Char
) {
    protected fun getAttrFormat(): CSVFormat {
        var format = CSVFormat.RFC4180
            .withRecordSeparator("")
            .withDelimiter(delimiter)
        if (quote != null) {
            format = format.withQuote(quote)
            format = format.withEscape('\\')
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        return format
    }

    protected fun getNameValueFormat(): CSVFormat {
        var format = CSVFormat.RFC4180
            .withRecordSeparator("")
            .withDelimiter(valueDelimiter)
        if (valueQuote != null) {
            format = format.withQuote(valueQuote)
            format = format.withEscape('\\')
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        return format
    }
}
