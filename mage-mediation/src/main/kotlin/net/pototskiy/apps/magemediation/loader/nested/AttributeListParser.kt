package net.pototskiy.apps.magemediation.loader.nested

import net.pototskiy.apps.magemediation.api.config.data.Field
import net.pototskiy.apps.magemediation.api.config.type.Attribute
import net.pototskiy.apps.magemediation.api.config.type.AttributeAttributeListType
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.QuoteMode

class AttributeListParser(
    private val data: String,
    private val field: Map.Entry<Field,Attribute>
) {
    private val attrs: Map<String, String> = parse()
    operator fun get(row: Int): Array<String> =
        when (row) {
            0 -> attrs.keys.toTypedArray()
            1 -> attrs.values.toTypedArray()
            else -> throw IndexOutOfBoundsException("${AttributeListParser::class.simpleName} has only two rows: 0 and 1")
        }

    private fun parse(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val type = field.value.type
        if (type is AttributeAttributeListType) {
            val nameValueFormat = getNameValueFormat(type)
            val attrFormat = getAttrFormat(type)
            val attrParser = CSVParser.parse(data, attrFormat)
            for (attr in attrParser.records[0]) {
                val parsed = CSVParser.parse(attr, nameValueFormat).records
                result[parsed[0][0]] = parsed[0][1]
            }
        }
        return result
    }

    private fun getAttrFormat(csvDef: AttributeAttributeListType): CSVFormat {
        var format = CSVFormat.RFC4180
        if (csvDef.delimiter.isNotBlank()) {
            format = format.withDelimiter(csvDef.delimiter[0])
        }
        if (csvDef.quote.isNotBlank()) {
            format = format.withQuote(csvDef.quote[0])
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        return format
    }

    private fun getNameValueFormat(csvDef: AttributeAttributeListType): CSVFormat {
        var format = CSVFormat.RFC4180

        if (csvDef.valueDelimiter.isNotBlank())
            format = format.withDelimiter(csvDef.valueDelimiter[0])
        if (csvDef.valueQuote.isBlank()) {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        } else {
            format = format.withQuote(csvDef.valueQuote[0])
        }
        return format
    }
}
