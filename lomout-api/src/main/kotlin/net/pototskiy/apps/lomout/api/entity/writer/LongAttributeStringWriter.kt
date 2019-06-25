package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.values.longToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default writer for [LONG] attribute
 *
 * @property locale String The value locale, default: system locale
 */
open class LongAttributeStringWriter : AttributeWriterPlugin<LONG>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun write(
        value: LONG?,
        cell: Cell
    ) {
        value?.let { cell.setCellValue(it.value.longToString(locale.createLocale())) }
    }
}
