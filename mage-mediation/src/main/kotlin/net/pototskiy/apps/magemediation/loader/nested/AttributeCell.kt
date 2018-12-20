package net.pototskiy.apps.magemediation.loader.nested

import net.pototskiy.apps.magemediation.loader.LoaderException
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellAddress
import net.pototskiy.apps.magemediation.source.CellType
import net.pototskiy.apps.magemediation.source.Row

class AttributeCell(
    private val _address: CellAddress,
    private val _value: String,
    private val _row: AttributeRow
) : Cell {
    override fun asString(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val address: CellAddress
        get() = _address
    override val cellType: CellType
        get() = CellType.STRING
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
    override val stringValue: String
        get() = _value
    override val row: Row
        get() = _row
}