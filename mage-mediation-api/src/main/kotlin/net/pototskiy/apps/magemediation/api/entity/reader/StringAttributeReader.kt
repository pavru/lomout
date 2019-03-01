package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.entity.StringValue
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class StringAttributeReader : AttributeReaderPlugin<StringType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun read(attribute: Attribute<out StringType>, input: Cell): StringType? =
        input.readString(locale.createLocale())?.let { StringValue(it) }
}
