package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default reader for [Boolean] attribute
 *
 * @property locale String The value locale, default: system locale. This is parameter
 */
open class BooleanAttributeReader : AttributeReader<Boolean?>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): Boolean? =
        input.readBoolean(locale.createLocale())?.let { it }
}
