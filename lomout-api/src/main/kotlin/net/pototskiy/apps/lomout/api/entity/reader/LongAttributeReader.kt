package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default reader for [Long] attribute
 *
 * @property locale The value locale. This is parameter
 * @property groupingUsed Indicate tha number uses digit grouping. This is parameter
 */
@Suppress("MemberVisibilityCanBePrivate")
open class LongAttributeReader : AttributeReader<Long?>() {
    var locale: String = DEFAULT_LOCALE_STR
    var groupingUsed = false

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): Long? =
        input.readLong(locale.createLocale(), groupingUsed)?.let { it }
}
