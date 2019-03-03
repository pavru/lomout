package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.NOT_IMPLEMENTED
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.joda.time.DateTime

class AttributeCell(
    override val address: CellAddress,
    private val backingValue: String,
    override val row: AttributeRow
) : Cell {

    override fun asString(): String = backingValue

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
    override val stringValue: String = backingValue

    override fun setCellValue(value: String) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Boolean) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Long) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Double) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: DateTime) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }
}
