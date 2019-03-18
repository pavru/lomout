package net.pototskiy.apps.lomout.api.source.nested

import net.pototskiy.apps.lomout.api.AppCellDataException
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.joda.time.DateTime

class NestedAttributeCell(
    override val address: CellAddress,
    private var backingValue: String,
    override val row: NestedAttributeRow
) : Cell {

    override fun asString(): String {
        return stringValue
    }

    override val cellType: CellType = CellType.STRING
    override val booleanValue: Boolean
        get() = throwNotSupportCellType()
    override val longValue: Long
        get() = throwNotSupportCellType()
    override val doubleValue: Double
        get() = throwNotSupportCellType()

    private fun throwNotSupportCellType(): Nothing {
        throw AppCellDataException("${NestedAttributeCell::class.simpleName} supports only string type value")
    }

    override val stringValue: String
        get() = backingValue

    override fun setCellValue(value: String) {
        backingValue = value
    }

    override fun setCellValue(value: Boolean) = throwNotSupportCellType()

    override fun setCellValue(value: Long) = throwNotSupportCellType()

    override fun setCellValue(value: Double) = throwNotSupportCellType()

    override fun setCellValue(value: DateTime) = throwNotSupportCellType()
}
