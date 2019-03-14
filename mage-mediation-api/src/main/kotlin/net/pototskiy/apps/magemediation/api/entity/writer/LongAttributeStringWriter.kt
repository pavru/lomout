package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.LongType
import net.pototskiy.apps.magemediation.api.entity.values.longToString
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class LongAttributeStringWriter : AttributeWriterPlugin<LongType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun write(
        value: LongType?,
        cell: Cell
    ) {
        value?.let { cell.setCellValue(it.value.longToString(locale.createLocale())) }
    }
}
