package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookType

class AttributeWorkbook(
    private val quote: String?,
    private val delimiter: String,
    private val valueQuote: String?,
    private val valueDelimiter: String,
    private val attributeName: String
) : Workbook {
    private val parser = AttributeListParser(quote, delimiter, valueQuote, valueDelimiter)
    private val printer = AttributeListPrinter(quote, delimiter, valueQuote, valueDelimiter)

    val cells = Array<MutableList<AttributeCell>>(2) { mutableListOf() }

    var string: String
        set(value) {
            cells.forEach { it.clear() }
            val sheet = AttributeSheet(this)
            val rows = arrayOf(
                AttributeRow(0, cells[0], sheet),
                AttributeRow(0, cells[1], sheet)
            )
            var column = 0
            parser.parse(value).forEach { key, value ->
                cells[0].add(column, AttributeCell(CellAddress(0, column), key, rows[0]))
                cells[1].add(column, AttributeCell(CellAddress(1, column), value, rows[1]))
                column++
            }
        }
        get() {
            return printer.print(
                cells[0].mapIndexed { c, attributeCell ->
                    attributeCell.stringValue to cells[1][c].stringValue
                }.toMap()
            )
        }

    override fun insertSheet(sheet: String): Sheet {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override val name: String
        get() = "workbook_for_$attributeName"
    override val type: WorkbookType
        get() = WorkbookType.ATTRIBUTE

    override fun get(sheet: String): Sheet = AttributeSheet(this)
    override fun get(sheet: Int): Sheet = AttributeSheet(this)
    override fun hasSheet(sheet: String): Boolean = true
    override fun iterator(): Iterator<Sheet> = AttributeSheetIterator(this)
    override fun close() {
        // nothing to close workbook use string
    }
}
