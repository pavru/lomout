package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.DoubleType
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default reader for [DoubleType] attribute
 *
 * @property locale The value locale: default: system locale
 * @property groupingUsed Indicate that number uses digit grouping
 */
@Suppress("MemberVisibilityCanBePrivate")
open class DoubleAttributeReader : AttributeReaderPlugin<DoubleType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var groupingUsed: Boolean = false

    override fun read(attribute: Attribute<out DoubleType>, input: Cell): DoubleType? {
        try {
            return input.readDouble(locale.createLocale(), groupingUsed)?.let { DoubleType(it) }
        } catch (e: AppDataException) {
            throw AppDataException(e.place + attribute, e.message, e)
        }
    }
}
