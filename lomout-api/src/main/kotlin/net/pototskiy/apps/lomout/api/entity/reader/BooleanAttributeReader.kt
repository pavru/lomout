package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.type.BOOLEAN
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default reader for [BOOLEAN] attribute
 *
 * @property locale String The value locale, default: system locale
 */
open class BooleanAttributeReader : AttributeReaderPlugin<BOOLEAN>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: Attribute<out BOOLEAN>, input: Cell): BOOLEAN? =
        input.readBoolean(locale.createLocale())?.let { BOOLEAN(it) }
}
