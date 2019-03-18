package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.DateTimeType
import net.pototskiy.apps.lomout.api.entity.values.datetimeToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

open class DateTimeAttributeStringWriter : AttributeWriterPlugin<DateTimeType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null

    override fun write(
        value: DateTimeType?,
        cell: Cell
    ) {
        value?.let { dateValue ->
            cell.setCellValue(
                pattern?.let {
                    dateValue.value.datetimeToString(it)
                } ?: dateValue.value.datetimeToString(locale.createLocale())
            )
        }
    }
}
