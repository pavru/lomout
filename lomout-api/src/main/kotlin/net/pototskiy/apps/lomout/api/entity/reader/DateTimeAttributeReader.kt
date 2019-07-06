package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import java.time.LocalDateTime

/**
 * Default reader for [LocalDateTime] attribute
 *
 * @property locale String The value locale, default: system locale. This is parameter
 * @property pattern String? The value pattern, optional (use locale). This is parameter
 */
open class DateTimeAttributeReader : AttributeReader<LocalDateTime?>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): LocalDateTime? =
        (pattern?.let { input.readeDateTimeWithPattern(attribute, it) }
            ?: input.readeDateTimeWithLocale(attribute, locale.createLocale()))?.let { it }
}
