package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookType

class NestedAttributeWorkbook(
    quote: String?,
    delimiter: String,
    valueQuote: String?,
    valueDelimiter: String,
    private val attributeName: String
) : Workbook {
    private val parser = NestedAttributeListParser(quote, delimiter, valueQuote, valueDelimiter)
    private val printer = NestedAttributeListPrinter(quote, delimiter, valueQuote, valueDelimiter)

    val cells = Array<MutableList<NestedAttributeCell>>(2) { mutableListOf() }

    var string: String
        set(value) {
            cells.forEach { it.clear() }
            val sheet = NestedAttributeSheet(this)
            val rows = arrayOf(
                NestedAttributeRow(0, cells[0], sheet),
                NestedAttributeRow(1, cells[1], sheet)
            )
            var column = 0
            parser.parse(value).forEach { key, valueData ->
                cells[0].add(column, NestedAttributeCell(CellAddress(0, column), key, rows[0]))
                cells[1].add(column, NestedAttributeCell(CellAddress(1, column), valueData, rows[1]))
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

    override fun get(sheet: String): Sheet = NestedAttributeSheet(this)
    override fun get(sheet: Int): Sheet = NestedAttributeSheet(this)
    override fun hasSheet(sheet: String): Boolean = true
    override fun iterator(): Iterator<Sheet> = NestedAttributeSheetIterator(this)
    override fun close() {
        // nothing to close workbook use string
    }
}
