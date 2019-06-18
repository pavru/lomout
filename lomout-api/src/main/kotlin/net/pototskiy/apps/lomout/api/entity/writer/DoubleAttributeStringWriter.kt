package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.type.DOUBLE
import net.pototskiy.apps.lomout.api.entity.values.doubleToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default writer for [DOUBLE] attribute
 *
 * @property locale String The value locale, default: system locale
 */
open class DoubleAttributeStringWriter : AttributeWriterPlugin<DOUBLE>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun write(value: DOUBLE?, cell: Cell) {
        value?.let { cell.setCellValue(it.value.doubleToString(locale.createLocale())) }
    }
}
