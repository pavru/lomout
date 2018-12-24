package net.pototskiy.apps.magemediation.source.csv

import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellAddress
import net.pototskiy.apps.magemediation.source.CellType
import net.pototskiy.apps.magemediation.source.Row

class CsvCell(
    private val _address: CellAddress,
    private val _value: String,
    private val _row: CsvRow
): Cell {
    override val address: CellAddress
        get() = _address
    override val cellType: CellType
        get() = _value.recognize()
    override val booleanValue: Boolean
        get() = _value.toBoolean()
    override val intValue: Long
        get() = _value.toLong()
    override val doubleValue: Double
        get() = _value.toDouble()
    override val stringValue: String
        get() = _value

    override fun asString(): String {
        return _value
    }

    override val row: Row
        get() = _row

    private fun String.recognize(): CellType {
        return when {
            this.toLongOrNull() != null -> CellType.INT
            this.toDoubleOrNull() != null -> CellType.DOUBLE
            this.toLowerCase().contains(Regex("^(true|false)$")) -> CellType.BOOL
            else -> CellType.STRING
        }
    }
}