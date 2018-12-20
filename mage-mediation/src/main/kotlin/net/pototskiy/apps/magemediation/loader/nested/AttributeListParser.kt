package net.pototskiy.apps.magemediation.loader.nested

import net.pototskiy.apps.magemediation.config.excel.AttrListDefinition
import net.pototskiy.apps.magemediation.config.excel.Field
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.QuoteMode

class AttributeListParser(
    private val data: String,
    private val fieldDef: Field
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
        val csvDef = fieldDef.typeDefinitions[0]
        if (csvDef is AttrListDefinition) {
            val nameValueFormat = getNameValueFormat(csvDef)
            val attrFormat = getAttrFormat(csvDef)
            val attrParser = CSVParser.parse(data, attrFormat)
            for (attr in attrParser.records[0]) {
                val parsed = CSVParser.parse(attr, nameValueFormat).records
                result[parsed[0][0]] = parsed[0][1]
            }
        }
        return result
    }

    private fun getAttrFormat(csvDef: AttrListDefinition): CSVFormat {
        var format = CSVFormat.RFC4180
        if (csvDef.attrDelimiter.isNotBlank()) {
            format = format.withDelimiter(csvDef.attrDelimiter[0])
        }
        if (csvDef.attrQuote.isNotBlank()) {
            format = format.withQuote(csvDef.attrQuote[0])
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        return format
    }

    private fun getNameValueFormat(csvDef: AttrListDefinition): CSVFormat {
        var format = CSVFormat.RFC4180

        if (csvDef.nameValueDelimiter.isNotBlank())
            format = format.withDelimiter(csvDef.nameValueDelimiter[0])
        if (csvDef.valueQuote.isBlank()) {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        } else {
            format = format.withQuote(csvDef.valueQuote[0])
        }
        return format
    }
}