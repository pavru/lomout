package net.pototskiy.apps.magemediation.api.source.workbook

interface Row : Iterable<Cell?> {
    val sheet: Sheet
    val rowNum: Int
    fun countCell(): Int
    operator fun get(column: Int): Cell?
    fun getOrEmptyCell(column: Int): Cell
}
