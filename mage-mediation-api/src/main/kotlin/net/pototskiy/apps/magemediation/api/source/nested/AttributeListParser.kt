package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.apache.commons.csv.CSVRecord
import java.io.StringReader

class AttributeListParser(
    quote: String?,
    delimiter: String,
    valueQuote: String?,
    valueDelimiter: String
) : AttributeListFormat(quote, delimiter, valueQuote, valueDelimiter) {

    fun parse(string: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        try {
            string.reader().use { attrReader ->
                parsePairsList(attrReader, result)
            }
        } catch (e: SourceException) {
            throw SourceException("Can not parse attribute list<$string>")
        }
//        val nameValueFormat = getNameValueFormat()
//        val attrFormat = getAttrFormat()
//        val attrRecords = CSVParser.parse(string, attrFormat).records
//        if (attrRecords.isNotEmpty()) {
//            for (attr in attrRecords[0]) {
//                val valueRecords = CSVParser.parse(attr, nameValueFormat).records
//                if (valueRecords.isNotEmpty() && valueRecords[0].size() == 2) {
//                    result[valueRecords[0][0]] = valueRecords[0][1]
//                }
//            }
//        }
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
            throw SourceException("Can not parse attribute list")
        }
    }
}
