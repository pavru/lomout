package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.joda.time.DateTime

class AttributeCell(
    override val address: CellAddress,
    private val _value: String,
    override val row: AttributeRow
) : Cell {

    override fun asString(): String = _value

    override val cellType: CellType = CellType.STRING
    override val booleanValue: Boolean
        get() {
            throw SourceException("${AttributeCell::class.simpleName} supports only string type value")
        }
    override val longValue: Long
        get() {
            throw SourceException("${AttributeCell::class.simpleName} supports only string type value")
        }
    override val doubleValue: Double
        get() {
            throw SourceException("${AttributeCell::class.simpleName} supports only string type value")
        }
    override val stringValue: String = _value

    override fun setCellValue(value: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: DateTime) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
