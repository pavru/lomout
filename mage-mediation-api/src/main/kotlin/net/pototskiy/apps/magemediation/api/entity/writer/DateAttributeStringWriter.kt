package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.DateType
import net.pototskiy.apps.magemediation.api.entity.DateValue
import net.pototskiy.apps.magemediation.api.entity.values.dateToString
import net.pototskiy.apps.magemediation.api.entity.values.datetimeToString
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class DateAttributeStringWriter : AttributeWriterPlugin<DateType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null

    override fun write(
        value: DateType?,
        cell: Cell
    ) {
        (value as? DateValue)?.let {
            if (pattern != null) {
                cell.setCellValue(it.value.datetimeToString(pattern as String))
            } else {
                cell.setCellValue(it.value.dateToString(locale.createLocale()))
            }
        }
    }
}
