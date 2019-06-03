package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.DateListType
import net.pototskiy.apps.lomout.api.entity.DateType
import net.pototskiy.apps.lomout.api.entity.values.stringToDate
import net.pototskiy.apps.lomout.api.entity.values.stringToDateTime
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat

/**
 * Default reader for [DateListType] attribute
 *
 * @property locale String The value locale
 * @property pattern String? The value pattern, optional (use locale)
 * @property quote Char? The value quote, optional
 * @property delimiter Char The list delimiter, default:,
 */
open class DateListAttributeReader : AttributeReaderPlugin<DateListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: Char? = null
    var delimiter: Char = ','

    override fun read(attribute: Attribute<out DateListType>, input: Cell): DateListType? {
        return when (input.cellType) {
            CellType.STRING -> {
                val listValue = input.stringValue.reader().use { reader ->
                    try {
                        CSVFormat.RFC4180
                            .withQuote(quote)
                            .withDelimiter(delimiter)
                            .withRecordSeparator("")
                            .parse(reader)
                            .records
                            .map { it.toList() }.flatten()
                            .map { data ->
                                DateType(pattern?.let { data.stringToDateTime(it) }
                                    ?: data.stringToDate(locale.createLocale()))
                            }
                    } catch (e: AppDataException) {
                        throw AppDataException(badPlace(attribute) + input, e.message, e)
                    }
                }
                DateListType(listValue)
            }
            CellType.BLANK -> null
            else -> throw AppDataException(
                badPlace(input) + attribute,
                "Reading Date list from the cell is not supported."
            )
        }
    }
}
