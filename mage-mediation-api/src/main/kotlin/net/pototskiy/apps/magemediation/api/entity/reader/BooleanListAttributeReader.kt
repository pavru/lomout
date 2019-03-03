package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.BooleanListType
import net.pototskiy.apps.magemediation.api.entity.BooleanListValue
import net.pototskiy.apps.magemediation.api.entity.BooleanValue
import net.pototskiy.apps.magemediation.api.entity.values.checkAndRemoveQuote
import net.pototskiy.apps.magemediation.api.entity.values.stringToBoolean
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.plugable.PluginException
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType

open class BooleanListAttributeReader : AttributeReaderPlugin<BooleanListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: String? = null
    var delimiter: String = ","

    override fun read(attribute: Attribute<out BooleanListType>, input: Cell): BooleanListType? =
        when (input.cellType) {
            CellType.STRING -> BooleanListValue(
                BooleanListValue(
                    input.stringValue
                        .split(delimiter)
                        .checkAndRemoveQuote(quote)
                        .map { BooleanValue(it.stringToBoolean(locale.createLocale())) }
                )
            )
            CellType.BLANK -> null
            else -> throw PluginException("Reading Boolean from cell type<${input.cellType}}> is not supported, " +
                    "attribute<${attribute.name}:${attribute.valueType.simpleName}>")
        }
}
