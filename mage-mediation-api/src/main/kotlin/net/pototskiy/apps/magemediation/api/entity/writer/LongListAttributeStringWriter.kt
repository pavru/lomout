package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.LongListType
import net.pototskiy.apps.magemediation.api.entity.LongListValue
import net.pototskiy.apps.magemediation.api.entity.values.longToString
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class LongListAttributeStringWriter : AttributeWriterPlugin<LongListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: String? = null
    var delimiter: String = ","

    override fun write(attribute: Attribute<LongListType>, value: LongListType?, cell: Cell) {
        (value as? LongListValue)?.let { list ->
            list.value.joinToString(delimiter) {
                val v = it.value.longToString(locale.createLocale())
                "${quote ?: ""}$v${quote ?: ""}"
            }
        }
    }
}
