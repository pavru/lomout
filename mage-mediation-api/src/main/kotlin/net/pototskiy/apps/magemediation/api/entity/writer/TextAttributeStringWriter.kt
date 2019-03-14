package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.entity.TextType
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class TextAttributeStringWriter : AttributeWriterPlugin<TextType>() {
    override fun write(
        value: TextType?,
        cell: Cell
    ) {
        value?.let { cell.setCellValue(it.value) }
    }
}
