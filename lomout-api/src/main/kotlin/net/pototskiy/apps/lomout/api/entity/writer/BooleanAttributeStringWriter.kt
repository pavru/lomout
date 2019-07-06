package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default writer for [Boolean] attribute
 *
 * * true → 1
 * * false → 0
 *
 * @property locale String The value local, ignored. This is parameter
 */
open class BooleanAttributeStringWriter : AttributeWriter<Boolean?>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun write(value: Boolean?, cell: Cell) {
        value?.let { cell.setCellValue(if (it) "1" else "0") }
    }
}
