package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.values.datetimeToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import java.time.LocalDateTime

/**
 * Default writer for [LocalDateTime] attribute
 *
 * @property locale String The value locale, default: system locale. This is parameter
 * @property pattern String? The datetime locale,optional(use locale). This is parameter
 */
open class DateTimeAttributeStringWriter : AttributeWriter<LocalDateTime?>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null

    override fun write(value: LocalDateTime?, cell: Cell) {
        value?.let { dateValue ->
            cell.setCellValue(
                pattern?.let {
                    dateValue.datetimeToString(it)
                } ?: dateValue.datetimeToString(locale.createLocale())
            )
        }
    }
}
