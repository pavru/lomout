package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.TextType
import net.pototskiy.apps.magemediation.api.entity.TextValue
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class TextAttributeReader : AttributeReaderPlugin<TextType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: Attribute<out TextType>, input: Cell): TextType? =
        input.readString(locale.createLocale())?.let { TextValue(it) }

}
