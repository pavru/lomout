package net.pototskiy.apps.magemediation.loader.nested

import net.pototskiy.apps.magemediation.loader.LoaderException
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellAddress
import net.pototskiy.apps.magemediation.source.CellType

class AttributeCell(
    override val address: CellAddress,
    private val _value: String,
    override val row: AttributeRow
) : Cell {
    override fun asString(): String = _value

    override val cellType: CellType = CellType.STRING
    override val booleanValue: Boolean
        get() {
            throw LoaderException("${AttributeCell::class.simpleName} supports only string type value")
        }
    override val intValue: Long
        get() {
            throw LoaderException("${AttributeCell::class.simpleName} supports only string type value")
        }
    override val doubleValue: Double
        get() {
            throw LoaderException("${AttributeCell::class.simpleName} supports only string type value")
        }
    override val stringValue: String = _value
}