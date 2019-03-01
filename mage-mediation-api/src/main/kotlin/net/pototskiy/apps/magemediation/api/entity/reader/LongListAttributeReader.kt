package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.LongListType
import net.pototskiy.apps.magemediation.api.entity.LongListValue
import net.pototskiy.apps.magemediation.api.entity.LongValue
import net.pototskiy.apps.magemediation.api.entity.values.checkAndRemoveQuote
import net.pototskiy.apps.magemediation.api.entity.values.stringToLong
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.plugable.PluginException
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import java.text.ParseException

open class LongListAttributeReader : AttributeReaderPlugin<LongListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: String? = null
    var delimiter: String = ","

    override fun read(attribute: Attribute<out LongListType>, input: Cell): LongListType? = when (input.cellType) {
        CellType.STRING -> LongListValue(
            input.stringValue
                .split(delimiter)
                .checkAndRemoveQuote(quote)
                .map {
                    try {
                        LongValue(it.stringToLong(locale.createLocale()))
                    } catch (e: ParseException) {
                        throw PluginException(
                            "Value<$it> can not be converted to Long, " +
                                    "attribute<${attribute.name}:${attribute.valueType.simpleName}>",
                            e
                        )
                    }
                }
        )
        CellType.BLANK -> null
        CellType.LONG -> LongListValue(listOf(LongValue(input.longValue)))
        else -> throw PluginException("Reading long list from cell type<${input.cellType}> is not supported, " +
                "attribute<${attribute.name}:${attribute.valueType.simpleName}>")
    }
}
