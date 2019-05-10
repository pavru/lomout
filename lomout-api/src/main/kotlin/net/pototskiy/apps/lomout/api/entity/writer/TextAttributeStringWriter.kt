package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.entity.TextType
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default attribute writer for [TextType] attribute
 */
open class TextAttributeStringWriter : AttributeWriterPlugin<TextType>() {
    override fun write(
        value: TextType?,
        cell: Cell
    ) {
        value?.let { cell.setCellValue(it.value) }
    }
}
