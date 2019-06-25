package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.entity.values.datetimeToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default writer for [DATETIME] attribute
 *
 * @property locale String The value locale, default: system locale
 * @property pattern String? The datetime locale,optional(use locale)
 */
open class DateTimeAttributeStringWriter : AttributeWriterPlugin<DATETIME>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null

    override fun write(
        value: DATETIME?,
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
