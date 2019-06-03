package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.StringListType
import net.pototskiy.apps.lomout.api.entity.StringType
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat

/**
 * Default reader for [StringListType] attribute
 *
 * @property quote Char? The value quote, optional
 * @property delimiter Char The list delimiter: default:,
 */
open class StringListAttributeReader : AttributeReaderPlugin<StringListType>() {
    var quote: Char? = null
    var delimiter: Char = ','

    override fun read(attribute: Attribute<out StringListType>, input: Cell): StringListType? {
        return when (input.cellType) {
            CellType.STRING -> {
                val listValue = input.stringValue.reader().use { reader ->
                    CSVFormat.RFC4180
                        .withQuote(quote)
                        .withDelimiter(delimiter)
                        .withRecordSeparator("")
                        .parse(reader)
                        .records
                        .map { it.toList() }.flatten()
                        .map { StringType(it) }
                }
                StringListType(listValue)
            }
            CellType.BLANK -> null
            else -> throw AppDataException(
                badPlace(input) + attribute,
                "Reading String list from the cell is not supported."
            )
        }
    }
}
