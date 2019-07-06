package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import java.time.LocalDate

/**
 * Default reader for [LocalDate] attribute
 *
 * @property locale String The value locale. This is parameter
 * @property pattern String? The value pattern, optional (use locale). This is parameter
 */
open class DateAttributeReader : AttributeReader<LocalDate?>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): LocalDate? =
        (pattern?.let { input.readDateWithPattern(attribute, it) }
            ?: input.readDateWithLocale(attribute, locale.createLocale()))
}
