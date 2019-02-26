package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.BooleanListType
import net.pototskiy.apps.magemediation.api.entity.BooleanListValue
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class BooleanListAttributeStringWriter : AttributeWriterPlugin<BooleanListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: String? = null
    var delimiter: String = ","

    override fun write(attribute: Attribute<BooleanListType>, value: BooleanListType?, cell: Cell) {
        (value as? BooleanListValue)?.let { list ->
            cell.setCellValue(list.value.joinToString(delimiter) {
                "${quote ?: ""}${if (it.value) "1" else "0"}${quote ?: ""}"
            })
        }
    }
}
