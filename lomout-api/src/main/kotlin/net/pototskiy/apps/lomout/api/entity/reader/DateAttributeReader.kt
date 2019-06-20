package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default reader for [DATE] attribute
 *
 * @property locale String The value locale
 * @property pattern String? The value pattern, optional (use locale)
 */
open class DateAttributeReader : AttributeReaderPlugin<DATE>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null

    override fun read(attribute: Attribute<out DATE>, input: Cell): DATE? =
        (pattern?.let { input.readeDateTime(attribute, it) }
            ?: input.readeDateTime(attribute, locale.createLocale()))?.let { DATE(it) }
}
