package net.pototskiy.apps.lomout.api.source.nested

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.QuoteMode

/**
 * Nested attribute list format
 *
 * @property quote Char? The name-value pair quote, null - no quote
 * @property delimiter Char The name-value pair delimiter
 * @property valueQuote Char? The value quote, null - no quote
 * @property valueDelimiter Char The name value delimiter
 * @constructor
 */
open class NestedAttributeListFormat(
    private val quote: Char?,
    private val delimiter: Char,
    private val valueQuote: Char?,
    private val valueDelimiter: Char
) {
    /**
     * CSV format for name-value pairs list
     *
     * @return CSVFormat
     */
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

    /**
     * Get CSV format for name value
     *
     * @return CSVFormat
     */
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
