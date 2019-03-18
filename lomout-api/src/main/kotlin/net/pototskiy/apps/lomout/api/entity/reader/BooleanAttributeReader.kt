package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.BooleanType
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

open class BooleanAttributeReader : AttributeReaderPlugin<BooleanType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: Attribute<out BooleanType>, input: Cell): BooleanType? =
        input.readBoolean(locale.createLocale())?.let { BooleanType(it) }
}
