package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.DateListType
import net.pototskiy.apps.magemediation.api.entity.DateListValue
import net.pototskiy.apps.magemediation.api.entity.DateValue
import net.pototskiy.apps.magemediation.api.entity.values.checkAndRemoveQuote
import net.pototskiy.apps.magemediation.api.entity.values.stringToDate
import net.pototskiy.apps.magemediation.api.entity.values.stringToDateTime
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException

open class DateListAttributeReader : AttributeReaderPlugin<DateListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: String? = null
    var delimiter: String = ","

    override fun read(attribute: Attribute<out DateListType>, input: Cell): DateListType? {
        return when (input.cellType) {
            CellType.STRING -> DateListValue(
                    input.stringValue
                        .split(delimiter)
                        .checkAndRemoveQuote(quote)
                        .map { str ->
                            DateValue(pattern?.let { str.stringToDateTime(it) }
                                ?: str.stringToDate(locale.createLocale()))
                        }
            )
            CellType.BLANK -> null
            else -> throw SourceException("Reading Date list from cell type<${input.cellType}> is not supported, " +
                    "attribute<${attribute.name}:${attribute.valueType.simpleName}>")
        }
    }

}
