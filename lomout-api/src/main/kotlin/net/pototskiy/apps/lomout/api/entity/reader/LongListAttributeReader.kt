package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.LongListType
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.entity.values.stringToLong
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat
import java.text.ParseException

/**
 * Default reader for [LongListType] attribute
 *
 * @property locale The value locale
 * @property quote The value quote, optional
 * @property delimiter The list delimiter, default:,
 * @property groupingUsed Indicate that number uses digit grouping
 */
@Suppress("MemberVisibilityCanBePrivate")
open class LongListAttributeReader : AttributeReaderPlugin<LongListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var groupingUsed: Boolean = false
    var delimiter: Char = ','

    override fun read(attribute: Attribute<out LongListType>, input: Cell): LongListType? = when (input.cellType) {
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
                        .map { LongType(it.stringToLong(locale.createLocale(), groupingUsed)) }
                } catch (e: ParseException) {
                    throw AppDataException(badPlace(attribute) + input, e.message, e)
                }
            }
            LongListType(listValue)
        }
        CellType.BLANK -> null
        CellType.LONG -> LongListType(listOf(LongType(input.longValue)))
        else -> throw AppDataException(
            badPlace(input) + attribute,
            "Reading long list from the cell is not supported."
        )
    }
}
