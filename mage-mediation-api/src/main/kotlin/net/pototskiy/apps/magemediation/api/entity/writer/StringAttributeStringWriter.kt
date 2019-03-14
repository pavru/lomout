package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class StringAttributeStringWriter : AttributeWriterPlugin<StringType>() {
    override fun write(
        value: StringType?,
        cell: Cell
    ) {
        value?.let { cell.setCellValue(it.value) }
    }
}
