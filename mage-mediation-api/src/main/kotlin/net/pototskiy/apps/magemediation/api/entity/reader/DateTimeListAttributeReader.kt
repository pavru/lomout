package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.DateTimeListType
import net.pototskiy.apps.magemediation.api.entity.DateTimeListValue
import net.pototskiy.apps.magemediation.api.entity.DateTimeValue
import net.pototskiy.apps.magemediation.api.entity.values.checkAndRemoveQuote
import net.pototskiy.apps.magemediation.api.entity.values.stringToDateTime
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException

open class DateTimeListAttributeReader : AttributeReaderPlugin<DateTimeListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: String? = null
    var delimiter: String = ","

    override fun read(attribute: Attribute<out DateTimeListType>, input: Cell): DateTimeListType? =
        when (input.cellType) {
            CellType.STRING -> DateTimeListValue(
                input.stringValue
                    .split(delimiter)
                    .checkAndRemoveQuote(quote)
                    .map { str ->
                        DateTimeValue(pattern?.let { str.stringToDateTime(it) }
                            ?: str.stringToDateTime(locale.createLocale()))
                    }
            )
            CellType.BLANK -> null
            else -> throw SourceException("Reading DateTime list from cell type<${input.cellType}> is not supported, " +
                    "attribute<${attribute.name}:${attribute.valueType.simpleName}>")
        }
}
