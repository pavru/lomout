package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.BooleanType
import net.pototskiy.apps.magemediation.api.entity.BooleanValue
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class BooleanAttributeReader : AttributeReaderPlugin<BooleanType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: Attribute<out BooleanType>, input: Cell): BooleanType? =
        input.readBoolean(locale.createLocale())?.let { BooleanValue(it) }

}
