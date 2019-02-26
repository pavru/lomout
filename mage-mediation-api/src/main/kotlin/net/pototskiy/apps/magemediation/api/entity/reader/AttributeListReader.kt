package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.AttributeListValue
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.source.nested.AttributeListParser
import net.pototskiy.apps.magemediation.api.source.nested.AttributeWorkbook
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException

open class AttributeListReader : AttributeReaderPlugin<AttributeListType>() {
    var quote: String? = null
    var delimiter: String = ","
    var valueQuote: String? = null
    var valueDelimiter: String = "="

    override fun read(attribute: Attribute<out AttributeListType>, input: Cell): AttributeListType? {
        return when (input.cellType) {
            CellType.STRING -> {
                val attrs = AttributeWorkbook(
                    AttributeListParser(
                        input.stringValue,
                        quote,
                        delimiter,
                        valueQuote,
                        valueDelimiter
                    ),
                    attribute.name.attributeName
                )
                val names = attrs[0][0]
                    ?: throw SourceException("Can not read attributes from value<${input.stringValue}>, attribute<${attribute.name}:${attribute.valueType.simpleName}>")
                val values = attrs[0][1]
                    ?: throw SourceException("Can not read attributes from value<${input.stringValue}>, attribute<${attribute.name}:${attribute.valueType.simpleName}>")
                AttributeListValue(
                    AttributeListValue(
                        names.mapIndexedNotNull { c, cell ->
                            if (cell != null) {
                                cell.stringValue to values.getOrEmptyCell(c)
                            } else {
                                null
                            }
                        }.toMap()
                    )
                )
            }
            else -> throw SourceException("Reading attribute list from cell type<${input.cellType}> is not supported, attribute<${attribute.name}:${attribute.valueType.simpleName}>")
        }
    }
}
