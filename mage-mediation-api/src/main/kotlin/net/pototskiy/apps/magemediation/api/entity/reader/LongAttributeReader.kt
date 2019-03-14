package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.LongType
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class LongAttributeReader : AttributeReaderPlugin<LongType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: Attribute<out LongType>, input: Cell): LongType? =
        input.readLong(locale.createLocale())?.let { LongType(it) }
}
