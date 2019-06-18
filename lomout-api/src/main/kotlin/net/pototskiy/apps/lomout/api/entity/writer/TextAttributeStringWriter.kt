package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.entity.type.TEXT
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default attribute writer for [TEXT] attribute
 */
open class TextAttributeStringWriter : AttributeWriterPlugin<TEXT>() {
    override fun write(value: TEXT?, cell: Cell) {
        value?.let { cell.setCellValue(it.value) }
    }
}
