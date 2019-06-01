package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppCellDataException
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.AttributeListType
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.nested.NestedAttributeWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType

/**
 * Default reader for [AttributeListType] attribute
 *
 * @property quote Char? The name-value pair quote, optional
 * @property delimiter Char The delimiter between pairs, default: ,
 * @property valueQuote Char? The value quote, optional
 * @property valueDelimiter Char The delimiter between name and value, default: =
 */
open class AttributeListReader : AttributeReaderPlugin<AttributeListType>() {
    var quote: Char? = null
    var delimiter: Char = ','
    var valueQuote: Char? = null
    var valueDelimiter: Char = '='

    override fun read(attribute: Attribute<out AttributeListType>, input: Cell): AttributeListType? {
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
                AttributeListType(
                    names.mapIndexedNotNull { c, cell ->
                        if (cell != null) {
                            cell.stringValue to values.getOrEmptyCell(c)
                        } else {
                            null
                        }
                    }.toMap()
                )
            }
            CellType.BLANK -> return null
            else -> throw AppCellDataException(
                "Reading attribute list from cell type<${input.cellType}> " +
                        "is not supported, attribute<${attribute.name}:${attribute.valueType.simpleName}>"
            )
        }
    }
}
