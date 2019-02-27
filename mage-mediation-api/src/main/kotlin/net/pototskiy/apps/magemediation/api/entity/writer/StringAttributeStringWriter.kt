package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.entity.StringValue
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class StringAttributeStringWriter : AttributeWriterPlugin<StringType>() {
    override fun write(attribute: Attribute<StringType>, value: StringType?, cell: Cell) {
        (value as? StringValue)?.let { cell.setCellValue(it.value) }
    }
}