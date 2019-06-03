package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.DateTimeListType
import net.pototskiy.apps.lomout.api.entity.DateTimeType
import net.pototskiy.apps.lomout.api.entity.values.stringToDateTime
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat

/**
 * Default reader for [DateTimeListType] attribute
 *
 * @property locale String The value locale
 * @property pattern String? The value pattern, optional (use locale)
 * @property quote Char? The value quote, optional
 * @property delimiter Char The list delimiter, default:,
 */
open class DateTimeListAttributeReader : AttributeReaderPlugin<DateTimeListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: Char? = null
    var delimiter: Char = ','

    override fun read(attribute: Attribute<out DateTimeListType>, input: Cell): DateTimeListType? =
        when (input.cellType) {
            CellType.STRING -> {
                val listValue = input.stringValue.reader().use { reader ->
                    CSVFormat.RFC4180
                        .withQuote(quote)
                        .withDelimiter(delimiter)
                        .withRecordSeparator("")
                        .parse(reader)
                        .records
                        .map { it.toList() }.flatten()
                        .map { data ->
                            DateTimeType(
                                pattern?.let { data.stringToDateTime(it) }
                                    ?: data.stringToDateTime(locale.createLocale())
                            )
                        }
                }
                DateTimeListType(listValue)
            }
            CellType.BLANK -> null
            else -> throw AppDataException(
                badPlace(input) + attribute,
                "Reading DateTime list from the cell is not supported."
            )
        }
}
