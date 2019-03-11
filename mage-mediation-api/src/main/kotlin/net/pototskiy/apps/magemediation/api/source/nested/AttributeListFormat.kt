package net.pototskiy.apps.magemediation.api.source.nested

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.QuoteMode

open class AttributeListFormat(
    protected val quote: String?,
    protected val delimiter: String,
    protected val valueQuote: String?,
    protected val valueDelimiter: String
) {
    protected fun getAttrFormat(): CSVFormat {
        var format = CSVFormat.RFC4180.withRecordSeparator("")
        if (delimiter.isNotBlank()) {
            format = format.withDelimiter(delimiter[0])
        }
        if (quote?.isNotBlank() == true) {
            format = format.withQuote(quote[0])
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        return format
    }

    protected fun getNameValueFormat(): CSVFormat {
        var format = CSVFormat.RFC4180.withRecordSeparator("")

        if (valueDelimiter.isNotBlank())
            format = format.withDelimiter(valueDelimiter[0])
        if (valueQuote?.isBlank() == false) {
            format = format.withQuote(valueQuote[0])
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        return format
    }
}
