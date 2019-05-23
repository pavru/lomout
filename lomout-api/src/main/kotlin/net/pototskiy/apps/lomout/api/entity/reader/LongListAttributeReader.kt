package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppCellDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.LongListType
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.entity.values.stringToLong
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat

/**
 * Default reader for [LongListType] attribute
 *
 * @property locale String The value locale
 * @property quote Char? The value quote, optional
 * @property delimiter Char The list delimiter, default:,
 */
open class LongListAttributeReader : AttributeReaderPlugin<LongListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','

    override fun read(attribute: Attribute<out LongListType>, input: Cell): LongListType? = when (input.cellType) {
        CellType.STRING -> {
            val listValue = input.stringValue.reader().use { reader ->
                CSVFormat.RFC4180
                    .withQuote(quote)
                    .withDelimiter(delimiter)
                    .withRecordSeparator("")
                    .parse(reader)
                    .records
                    .map { it.toList() }.flatten()
                    .map { LongType(it.stringToLong(locale.createLocale())) }
            }
            LongListType(listValue)
        }
        CellType.BLANK -> null
        CellType.LONG -> LongListType(listOf(LongType(input.longValue)))
        else -> throw AppCellDataException(
            "Reading long list from cell type<${input.cellType}> is not supported, " +
                    "attribute<${attribute.name}:${attribute.valueType.simpleName}>"
        )
    }
}
