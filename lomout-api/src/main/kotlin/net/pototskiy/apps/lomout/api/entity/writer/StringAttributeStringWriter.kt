package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default attribute writer for [String] attribute
 */
open class StringAttributeStringWriter : AttributeWriter<String?>() {
    override fun write(value: String?, cell: Cell) {
        value?.let { cell.setCellValue(it) }
    }
}
