package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.DateType
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

open class DateAttributeReader : AttributeReaderPlugin<DateType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null

    override fun read(attribute: Attribute<out DateType>, input: Cell): DateType? =
        (pattern?.let { input.readeDateTime(attribute, it) }
            ?: input.readeDateTime(attribute, locale.createLocale()))?.let { DateType(it) }
}
