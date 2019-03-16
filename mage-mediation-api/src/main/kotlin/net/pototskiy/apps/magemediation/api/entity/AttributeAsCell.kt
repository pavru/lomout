package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.AppCellDataException
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.Row
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.joda.time.DateTime

class AttributeAsCell<T : Type>(
    attribute: Attribute<out T>,
    aValue: T?
) : Cell {
    private var cellValue: CellValue? = null

    init {
        @Suppress("UNCHECKED_CAST")
        (attribute.writer as AttributeWriter<T>).write(aValue, this)
    }

    override val address = CellAddress(0, 0)
    override val cellType: CellType
        get() = when (cellValue) {
            is CellStringValue -> CellType.STRING
            is CellBooleanValue -> CellType.BOOL
            is CellLongValue -> CellType.LONG
            is CellDoubleValue -> CellType.DOUBLE
            null -> CellType.BLANK
        }

    override val booleanValue: Boolean
        get() = when (val v = cellValue) {
            is CellBooleanValue -> v.value
            else -> throw AppCellDataException(DATA_INCOMPATIBLE_MSG)
        }
    override val longValue: Long
        get() = when (val v = cellValue) {
            is CellLongValue -> v.value
            else -> throw AppCellDataException(DATA_INCOMPATIBLE_MSG)
        }
    override val doubleValue: Double
        get() = when (val v = cellValue) {
            is CellDoubleValue -> v.value
            else -> throw AppCellDataException(DATA_INCOMPATIBLE_MSG)
        }
    override val stringValue: String
        get() = when (val v = cellValue) {
            is CellStringValue -> v.value
            else -> throw AppCellDataException(DATA_INCOMPATIBLE_MSG)
        }
    override val row: Row
        get() = throw NotImplementedError("Attribute cell has no row")

    override fun asString(): String {
        return cellValue?.value?.toString() ?: ""
    }

    override fun setCellValue(value: String) {
        cellValue = CellStringValue(value)
    }

    override fun setCellValue(value: Boolean) {
        cellValue = CellBooleanValue(value)
    }

    override fun setCellValue(value: Long) {
        cellValue = CellLongValue(value)
    }

    override fun setCellValue(value: Double) {
        cellValue = CellDoubleValue(value)
    }

    override fun setCellValue(value: DateTime) {
        cellValue = CellDoubleValue(HSSFDateUtil.getExcelDate(value.toDate()))
    }

    companion object {
        const val DATA_INCOMPATIBLE_MSG = "Data type is incompatible"
    }
}

private sealed class CellValue {
    abstract val value: Any
}

private class CellStringValue(override val value: String) : CellValue()
private class CellBooleanValue(override val value: Boolean) : CellValue()
private class CellLongValue(override val value: Long) : CellValue()
private class CellDoubleValue(override val value: Double) : CellValue()
