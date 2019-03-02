package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.DoubleListType
import net.pototskiy.apps.magemediation.api.entity.DoubleListValue
import net.pototskiy.apps.magemediation.api.entity.DoubleValue
import net.pototskiy.apps.magemediation.api.entity.values.checkAndRemoveQuote
import net.pototskiy.apps.magemediation.api.entity.values.stringToDouble
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.plugable.PluginException
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import java.text.ParseException

open class DoubleListAttributeReader : AttributeReaderPlugin<DoubleListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: String? = null
    var delimiter: String = ","

    override fun read(attribute: Attribute<out DoubleListType>, input: Cell): DoubleListType? =
        when (input.cellType) {
            CellType.STRING -> DoubleListValue(
                input.stringValue
                    .split(delimiter)
                    .checkAndRemoveQuote(quote)
                    .map { stringValue ->
                        try {
                            DoubleValue(stringValue.stringToDouble(locale.createLocale()))
                        } catch (e: ParseException) {
                            throw PluginException("Value<$stringValue> can not be converted to Double, " +
                                    "attribute<${attribute.name}:${attribute.valueType.simpleName}>")
                        }
                    }
            )
            CellType.BLANK -> null
            else -> throw PluginException("Reading Double from cell type<${input.cellType}> is not supported, " +
                    "attribute<${attribute.name}:${attribute.valueType.simpleName}>")
        }
}
