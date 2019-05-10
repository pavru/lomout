package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.entity.StringType
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default attribute writer for [StringType] attribute
 */
open class StringAttributeStringWriter : AttributeWriterPlugin<StringType>() {
    override fun write(
        value: StringType?,
        cell: Cell
    ) {
        value?.let { cell.setCellValue(it.value) }
    }
}
