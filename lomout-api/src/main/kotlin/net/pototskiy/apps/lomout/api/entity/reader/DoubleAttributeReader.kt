package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default reader for [Double] attribute
 *
 * @property locale The value locale: default: system locale. This is parameter
 * @property groupingUsed Indicate that number uses digit grouping. This is parameter
 */
@Suppress("MemberVisibilityCanBePrivate")
open class DoubleAttributeReader : AttributeReader<Double?>() {
    var locale: String = DEFAULT_LOCALE_STR
    var groupingUsed: Boolean = false

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): Double? {
        try {
            return input.readDouble(locale.createLocale(), groupingUsed)?.let { it }
        } catch (e: AppDataException) {
            throw AppDataException(e.place + attribute, e.message, e)
        }
    }
}
