package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.TextType
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

open class TextAttributeReader : AttributeReaderPlugin<TextType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: Attribute<out TextType>, input: Cell): TextType? =
        input.readString(locale.createLocale())?.let { TextType(it) }
}
