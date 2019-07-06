package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.values.dateToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import java.time.LocalDate

/**
 * Default writer for [LocalDate] attribute
 *
 * @property locale String The value locale, default: system locale. This is parameter
 * @property pattern String? The date pattern, optional(use locale). This is parameter
 */
open class DateAttributeStringWriter : AttributeWriter<LocalDate?>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null

    override fun write(value: LocalDate?, cell: Cell) {
        value?.let { dateValue ->
            pattern?.let {
                cell.setCellValue(dateValue.dateToString(it))
            } ?: cell.setCellValue(dateValue.dateToString(locale.createLocale()))
        }
    }
}
