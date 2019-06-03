package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.DoubleListType
import net.pototskiy.apps.lomout.api.entity.DoubleType
import net.pototskiy.apps.lomout.api.entity.values.stringToDouble
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat
import java.text.ParseException

/**
 * Default reader for [DoubleListType] attribute
 *
 * @property locale The value locale, default: system locale
 * @property quote The value quote, optional
 * @property delimiter The list delimiter, default:,
 * @property groupingUsed Indicate that number uses digit grouping
 */
@Suppress("MemberVisibilityCanBePrivate")
open class DoubleListAttributeReader : AttributeReaderPlugin<DoubleListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','
    var groupingUsed: Boolean = false

    override fun read(attribute: Attribute<out DoubleListType>, input: Cell): DoubleListType? =
        when (input.cellType) {
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
                            .map { DoubleType(it.stringToDouble(locale.createLocale(), groupingUsed)) }
                    } catch (e: ParseException) {
                        throw AppDataException(badPlace(attribute) + input, e.message, e)
                    }
                }
                DoubleListType(listValue)
            }
            CellType.BLANK -> null
            else -> throw AppDataException(
                badPlace(input) + attribute,
                "Reading Double from the cell is not supported."
            )
        }
}
