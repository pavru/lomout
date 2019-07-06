package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.values.stringToLong
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat
import java.text.ParseException

/**
 * Default reader for **List<Long>> attribute
 *
 * @property locale The value locale. This is parameter
 * @property quote The value quote, optional. This is parameter
 * @property delimiter The list delimiter, default:','. This is parameter
 * @property groupingUsed Indicate that number uses digit grouping. This is parameter
 */
@Suppress("MemberVisibilityCanBePrivate")
open class LongListAttributeReader : AttributeReader<List<Long>?>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var groupingUsed: Boolean = false
    var delimiter: Char = ','

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): List<Long>? = when (input.cellType) {
        CellType.STRING -> {
            input.stringValue.reader().use { reader ->
                try {
                    CSVFormat.RFC4180
                        .withQuote(quote)
                        .withDelimiter(delimiter)
                        .withRecordSeparator("")
                        .parse(reader)
                        .records
                        .map { it.toList() }.flatten()
                        .map {
                            it.stringToLong(locale.createLocale(), groupingUsed)
                        }
                } catch (e: ParseException) {
                    throw AppDataException(badPlace(attribute) + input, e.message, e)
                }
            }
        }
        CellType.BLANK -> null
        CellType.LONG -> listOf(input.longValue)
        else -> throw AppDataException(
            badPlace(input) + attribute,
            "Reading long list from the cell is not supported."
        )
    }
}
