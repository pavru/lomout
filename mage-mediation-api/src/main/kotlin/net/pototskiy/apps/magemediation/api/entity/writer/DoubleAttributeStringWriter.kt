package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.DoubleType
import net.pototskiy.apps.magemediation.api.entity.values.doubleToString
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class DoubleAttributeStringWriter : AttributeWriterPlugin<DoubleType>() {
    var locale: String = DEFAULT_LOCALE_STR

    override fun write(
        value: DoubleType?,
        cell: Cell
    ) {
        value?.let { cell.setCellValue(it.value.doubleToString(locale.createLocale())) }
    }
}
