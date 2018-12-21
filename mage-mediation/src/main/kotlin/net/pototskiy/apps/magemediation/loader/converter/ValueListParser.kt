package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.config.excel.ListDefinition
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.QuoteMode

class ValueListParser(
    private val valueList: String,
    private val listDef: ListDefinition
) {
    fun parse(): List<String> {
        var format = CSVFormat.RFC4180
        if (listDef.quote.isNotBlank()) {
            format = format.withQuote(listDef.quote[0])
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        if (listDef.delimiter.isNotBlank()) {
            format = format.withDelimiter(listDef.delimiter[0])
        }
        val records = CSVParser.parse(valueList, format).records
        return if (records.size == 0) {
            listOf()
        } else {
            records[0].map { it }
        }

    }
}