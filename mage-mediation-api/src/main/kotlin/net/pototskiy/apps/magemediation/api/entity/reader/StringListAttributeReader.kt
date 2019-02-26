package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.StringListType
import net.pototskiy.apps.magemediation.api.entity.StringListValue
import net.pototskiy.apps.magemediation.api.entity.StringValue
import net.pototskiy.apps.magemediation.api.entity.values.checkAndRemoveQuote
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException

open class StringListAttributeReader : AttributeReaderPlugin<StringListType>() {
    var quote: String? = null
    var delimiter: String = ","

    override fun read(attribute: Attribute<out StringListType>, input: Cell): StringListType? {
        return when (input.cellType) {
            CellType.STRING -> StringListValue(
                    input.stringValue
                        .split(delimiter)
                        .checkAndRemoveQuote(quote)
                        .map { StringValue(it) }
            )
            CellType.BLANK -> null
            else -> throw SourceException("Reading String list from cell type<${input.cellType}> is not supported, " +
                    "attribute<${attribute.name}:${attribute.valueType.simpleName}>")
        }
    }
}
