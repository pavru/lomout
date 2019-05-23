package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppCellDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.DoubleListType
import net.pototskiy.apps.lomout.api.entity.DoubleType
import net.pototskiy.apps.lomout.api.entity.values.stringToDouble
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat

/**
 * Default reader for [DoubleListType] attribute
 *
 * @property locale String The value locale, default: system locale
 * @property quote Char? The value quote, optional
 * @property delimiter Char The list delimiter, default:,
 */
open class DoubleListAttributeReader : AttributeReaderPlugin<DoubleListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','

    override fun read(attribute: Attribute<out DoubleListType>, input: Cell): DoubleListType? =
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
                        .map { DoubleType(it.stringToDouble(locale.createLocale())) }
                }
                DoubleListType(listValue)
            }
            CellType.BLANK -> null
            else -> throw AppCellDataException(
                "Reading Double from cell type<${input.cellType}> is not supported, " +
                        "attribute<${attribute.name}:${attribute.valueType.simpleName}>"
            )
        }
}
