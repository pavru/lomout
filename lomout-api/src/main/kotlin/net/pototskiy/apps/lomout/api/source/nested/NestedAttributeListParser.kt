package net.pototskiy.apps.lomout.api.source.nested

import net.pototskiy.apps.lomout.api.AppDataException
import org.apache.commons.csv.CSVRecord
import java.io.StringReader

/**
 * Nested attributes list parser
 *
 * @constructor
 * @param quote Char? The name-value pair quote, null - no quote
 * @param delimiter Char The name-value pair delimiter
 * @param valueQuote Char? The value quote, null - no quote
 * @param valueDelimiter Char The delimiter between name and value
 */
class NestedAttributeListParser(
    quote: Char?,
    delimiter: Char,
    valueQuote: Char?,
    valueDelimiter: Char
) : NestedAttributeListFormat(quote, delimiter, valueQuote, valueDelimiter) {

    /**
     * Parse list string to map of name->value
     *
     * @param string String The list as string
     * @return Map<String, String> The map of name->value
     */
    fun parse(string: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        try {
            string.reader().use { attrReader ->
                parsePairsList(attrReader, result)
            }
        } catch (e: AppDataException) {
            throw AppDataException("Can not parse attribute list<$string>")
        }
        return result
    }

    private fun parsePairsList(attrReader: StringReader, result: MutableMap<String, String>) {
        return getAttrFormat().parse(attrReader).use { attrParser ->
            attrParser.records.firstOrNull()?.forEach { attr ->
                attr?.reader()?.use { valueReader ->
                    parseNameValue(valueReader, result)
                }
            }
        }
    }

    private fun parseNameValue(valueReader: StringReader, result: MutableMap<String, String>) {
        return getNameValueFormat().parse(valueReader).use { valueParser ->
            valueParser.records.firstOrNull()?.let {
                addToResultMap(result, it)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun addToResultMap(result: MutableMap<String, String>, it: CSVRecord) {
        try {
            result[it[0]] = try {
                it[1]
            } catch (e: Exception) {
                ""
            }
        } catch (e: Exception) {
            throw AppDataException("Can not parse attribute list")
        }
    }
}
