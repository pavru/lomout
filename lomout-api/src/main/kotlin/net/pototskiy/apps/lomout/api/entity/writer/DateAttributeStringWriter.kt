package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.DateType
import net.pototskiy.apps.lomout.api.entity.values.dateToString
import net.pototskiy.apps.lomout.api.entity.values.datetimeToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default writer for [DateType] attribute
 *
 * @property locale String The value locale, default: system locale
 * @property pattern String? The date pattern, optional(use locale)
 */
open class DateAttributeStringWriter : AttributeWriterPlugin<DateType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null

    override fun write(
        value: DateType?,
        cell: Cell
    ) {
        value?.let { dateValue ->
            pattern?.let {
                cell.setCellValue(dateValue.value.datetimeToString(it))
            } ?: cell.setCellValue(dateValue.value.dateToString(locale.createLocale()))
        }
    }
}
