package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.nested.AttributeWorkbook
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class AttributeListStringWriter : AttributeWriterPlugin<AttributeListType>() {
    var quote: String? = null
    var delimiter: String = ","
    var valueQuote: String? = null
    var valueDelimiter: String = "="

    override fun write(
        value: AttributeListType?,
        cell: Cell
    ) {
        val workbook = AttributeWorkbook(quote, delimiter, valueQuote, valueDelimiter, "attributeWriter")
        val sheet = workbook["default"]
        val rows = arrayOf(sheet[0], sheet[1])
        var column = 0
        value?.value?.forEach { attr, attrValue ->
            rows[0]?.insertCell(column)?.setCellValue(attr)
            rows[1]?.insertCell(column)?.setCellValue(attrValue.stringValue)
            column++
        }
        cell.setCellValue(workbook.string)
    }
}
