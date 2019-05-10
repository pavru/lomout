package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.DoubleType
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default reader for [DoubleType] attribute
 *
 * @property locale String The value locale: default: system locale
 */
open class DoubleAttributeReader : AttributeReaderPlugin<DoubleType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: Attribute<out DoubleType>, input: Cell): DoubleType? =
        input.readDouble(locale.createLocale())?.let { DoubleType(it) }
}
