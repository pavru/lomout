package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.entity.values.longToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

open class LongAttributeStringWriter : AttributeWriterPlugin<LongType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun write(
        value: LongType?,
        cell: Cell
    ) {
        value?.let { cell.setCellValue(it.value.longToString(locale.createLocale())) }
    }
}
