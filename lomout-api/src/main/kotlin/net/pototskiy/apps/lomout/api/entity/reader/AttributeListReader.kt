package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.type.ATTRIBUTELIST
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.nested.NestedAttributeWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType

/**
 * Default reader for [ATTRIBUTELIST] attribute
 *
 * @property quote Char? The name-value pair quote, optional
 * @property delimiter Char The delimiter between pairs, default: ,
 * @property valueQuote Char? The value quote, optional
 * @property valueDelimiter Char The delimiter between name and value, default: =
 */
open class AttributeListReader : AttributeReaderPlugin<ATTRIBUTELIST>() {
    var quote: Char? = null
    var delimiter: Char = ','
    var valueQuote: Char? = null
    var valueDelimiter: Char = '='

    override fun read(attribute: Attribute<out ATTRIBUTELIST>, input: Cell): ATTRIBUTELIST? {
        return when (input.cellType) {
            CellType.STRING -> {
                if (input.stringValue.isBlank()) return null
                val attrs = NestedAttributeWorkbook(
                    quote,
                    delimiter,
                    valueQuote,
                    valueDelimiter,
                    attribute.name
                )
                attrs.string = input.stringValue
                val names = attrs[0][0]!!
                val values = attrs[0][1]!!
                ATTRIBUTELIST(
                    names.mapIndexedNotNull { c, cell ->
                        if (cell != null) {
                            cell.stringValue to values.getOrEmptyCell(c)
                        } else {
                            null
                        }
                    }.toMap()
                )
            }
            CellType.BLANK -> null
            else -> throw AppDataException(
                badPlace(input) + attribute,
                "Cannot read attribute list from the cell."
            )
        }
    }
}
