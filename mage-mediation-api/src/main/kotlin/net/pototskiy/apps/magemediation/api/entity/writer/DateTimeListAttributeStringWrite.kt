package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.entity.DateTimeListType
import net.pototskiy.apps.magemediation.api.entity.DateTimeListValue
import net.pototskiy.apps.magemediation.api.entity.values.datetimeToString
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class DateTimeListAttributeStringWrite : AttributeWriterPlugin<DateTimeListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: String? = null
    var delimiter: String = ","

    override fun write(
        value: DateTimeListType?,
        cell: Cell
    ) {
        (value as? DateTimeListValue)?.let { list ->
            list.value.joinToString(delimiter) {
                val v = when {
                    pattern != null -> it.value.datetimeToString(pattern as String)
                    else -> it.value.datetimeToString(locale)
                }
                "${quote ?: ""}$v${quote ?: ""}"
            }
        }
    }
}
