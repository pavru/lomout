package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default reader for [STRING] attribute
 *
 * @property locale String The value locale, default: system locale
 */
open class StringAttributeReader : AttributeReaderPlugin<STRING>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: Attribute<out STRING>, input: Cell): STRING? =
        input.readString(locale.createLocale())?.let { STRING(it) }
}
