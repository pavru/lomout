package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default reader for [LongType] attribute
 *
 * @property locale The value locale
 * @property groupingUsed Indicate tha number uses digit grouping
 */
@Suppress("MemberVisibilityCanBePrivate")
open class LongAttributeReader : AttributeReaderPlugin<LongType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var groupingUsed = false

    override fun read(attribute: Attribute<out LongType>, input: Cell): LongType? =
        input.readLong(locale.createLocale(), groupingUsed)?.let { LongType(it) }
}
