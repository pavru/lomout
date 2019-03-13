package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.CSV_SHEET_NAME
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.nested.NestedAttributeSheet
import net.pototskiy.apps.magemediation.api.source.nested.NestedAttributeWorkbook
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

open class AttributeListStringWriter : AttributeWriterPlugin<AttributeListType>() {
    var quote: Char? = null
    var delimiter: Char = ','
    var valueQuote: Char? = null
    var valueDelimiter: Char = '='

    override fun write(
        value: AttributeListType?,
        cell: Cell
    ) {
        val workbook = NestedAttributeWorkbook(quote, delimiter, valueQuote, valueDelimiter, "attributeWriter")
        val sheet = workbook[CSV_SHEET_NAME] as NestedAttributeSheet
        val rows = arrayOf(sheet[0], sheet[1])
        var column = 0
        value?.value?.forEach { attr, attrValue ->
            rows[0].insertCell(column).setCellValue(attr)
            rows[1].insertCell(column).setCellValue(attrValue.stringValue)
            column++
        }
        cell.setCellValue(workbook.string)
    }
}
