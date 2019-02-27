package net.pototskiy.apps.magemediation.api.source.nested

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.QuoteMode

class AttributeListParser(
    private val data: String,
    private val quote: String?,
    private val delimiter: String,
    private val valueQuote: String?,
    private val valueDelimiter: String
) {
// TODO remove
//    constructor(data: String, field: Map.Entry<Field, Attribute<*>>) : this(
//        data,
//        field.value.type.quote,
//        field.value.type.delimiter,
//        field.value.type.valueQuote,
//        field.value.type.valueDelimiter
//    )

    private val attrs: Map<String, String> = parse()
    operator fun get(row: Int): Array<String> =
        when (row) {
            0 -> attrs.keys.toTypedArray()
            1 -> attrs.values.toTypedArray()
            else -> throw IndexOutOfBoundsException("${AttributeListParser::class.simpleName} has only two rows: 0 and 1")
        }

    private fun parse(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val nameValueFormat = getNameValueFormat()
        val attrFormat = getAttrFormat()
        val attrParser = CSVParser.parse(data, attrFormat)
        for (attr in attrParser.records[0]) {
            val parsed = CSVParser.parse(attr, nameValueFormat).records
            result[parsed[0][0]] = parsed[0][1]
        }
        return result
    }

    private fun getAttrFormat(): CSVFormat {
        var format = CSVFormat.RFC4180
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

    private fun getNameValueFormat(): CSVFormat {
        var format = CSVFormat.RFC4180

        if (valueDelimiter.isNotBlank())
            format = format.withDelimiter(valueDelimiter[0])
        if (valueQuote?.isBlank() == true) {
            format = format.withQuote(valueQuote[0])
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        return format
    }
}
