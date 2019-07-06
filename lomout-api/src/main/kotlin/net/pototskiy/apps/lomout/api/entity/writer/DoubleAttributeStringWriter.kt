package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.values.doubleToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default writer for [Double] attribute
 *
 * @property locale String The value locale, default: system locale. This is parameter
 */
open class DoubleAttributeStringWriter : AttributeWriter<Double?>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun write(value: Double?, cell: Cell) {
        value?.let { cell.setCellValue(it.doubleToString(locale.createLocale())) }
    }
}
