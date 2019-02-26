package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.DoubleType
import net.pototskiy.apps.magemediation.api.entity.DoubleValue
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class DoubleAttributeReader : AttributeReaderPlugin<DoubleType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: Attribute<out DoubleType>, input: Cell): DoubleType? =
        input.readDouble(locale.createLocale())?.let { DoubleValue(it) }
}
