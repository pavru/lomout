package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default attribute writer for [STRING] attribute
 */
open class StringAttributeStringWriter : AttributeWriterPlugin<STRING>() {
    override fun write(value: STRING?, cell: Cell) {
        value?.let { cell.setCellValue(it.value) }
    }
}
