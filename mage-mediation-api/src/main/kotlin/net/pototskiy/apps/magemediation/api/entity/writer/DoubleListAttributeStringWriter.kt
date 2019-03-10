package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.DoubleListType
import net.pototskiy.apps.magemediation.api.entity.DoubleListValue
import net.pototskiy.apps.magemediation.api.entity.values.doubleToString
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class DoubleListAttributeStringWriter : AttributeWriterPlugin<DoubleListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: String? = null
    var delimiter: String = ","

    override fun write(
        value: DoubleListType?,
        cell: Cell
    ) {
        (value as? DoubleListValue)?.let { list ->
            list.value.joinToString(delimiter) {
                val v = it.value.doubleToString(locale.createLocale())
                "${quote ?: ""}$v${quote ?: ""}"
            }
        }
    }
}
