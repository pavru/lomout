package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.NOT_IMPLEMENTED
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class AttributeListStringWriter : AttributeWriterPlugin<AttributeListType>() {
    var quote: String? = null
    var delimiter: String = ","
    var valueQuote: String? = null
    var valueDelimiter: String = "="

    override fun write(attribute: Attribute<AttributeListType>, value: AttributeListType?, cell: Cell) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }
}
