package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.config.newOne.type.AttributeType
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.QuoteMode

class ValueListParser(
    private val valueList: String,
    private val type: AttributeType
) {
    fun parse(): List<String> {
        var format = CSVFormat.RFC4180
        if (type.quote.isNotBlank()) {
            format = format.withQuote(type.quote[0])
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        if (type.delimiter.isNotBlank()) {
            format = format.withDelimiter(type.delimiter[0])
        }
        val records = CSVParser.parse(valueList, format).records
        return if (records.size == 0) {
            listOf()
        } else {
            records[0].toList()
        }

    }
}