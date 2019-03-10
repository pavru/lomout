package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.entity.StringListType
import net.pototskiy.apps.magemediation.api.entity.StringListValue
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class StringListAttributeStringWriter : AttributeWriterPlugin<StringListType>() {
    var quote: String? = null
    var delimiter: String = ","

    override fun write(
        value: StringListType?,
        cell: Cell
    ) {
        (value as? StringListValue)?.let { list ->
            list.value.joinToString(delimiter) {
                "${quote ?: ""}$it${quote ?: ""}"
            }
        }
    }
}
