package net.pototskiy.apps.magemediation.source

interface Cell {
    val address: CellAddress
    val cellType: CellType
    val booleanValue: Boolean
    val intValue: Long
    val doubleValue: Double
    val stringValue: String
    val row: Row

    fun asString(): String
}