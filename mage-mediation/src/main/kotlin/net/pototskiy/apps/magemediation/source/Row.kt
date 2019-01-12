package net.pototskiy.apps.magemediation.source

interface Row : Iterable<Cell> {
    val sheet: Sheet
    val rowNum: Int
    fun countCell(): Int
    operator fun get(column: Int): Cell?
    fun getOrEmptyCell(column: Int): Cell
}