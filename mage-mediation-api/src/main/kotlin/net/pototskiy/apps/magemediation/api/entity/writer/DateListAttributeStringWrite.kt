package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.DateListType
import net.pototskiy.apps.magemediation.api.entity.DateListValue
import net.pototskiy.apps.magemediation.api.entity.values.dateToString
import net.pototskiy.apps.magemediation.api.entity.values.datetimeToString
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class DateListAttributeStringWrite : AttributeWriterPlugin<DateListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: String? = null
    var delimiter: String = ","

    override fun write(
        value: DateListType?,
        cell: Cell
    ) {
        (value as? DateListValue)?.let { list ->
            cell.setCellValue(list.value.joinToString(delimiter) {
                val v = when {
                    pattern != null -> it.value.datetimeToString(pattern as String)
                    else -> it.value.dateToString(locale.createLocale())
                }
                "${quote ?: ""}$v${quote ?: ""}"
            })
        }
    }
}
