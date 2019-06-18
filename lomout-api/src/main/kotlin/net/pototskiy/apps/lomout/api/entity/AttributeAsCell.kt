package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Row
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.joda.time.DateTime

/**
 * Present attribute as workbook cell
 *
 * @param T The attribute type
 * @property cellValue The cell value
 * @property address The cell address
 * @property cellType The cell type
 * @property booleanValue The cell boolean value
 * @property longValue The cell long value
 * @property doubleValue The cell double value
 * @property stringValue The cell string value
 * @property row The cell row
 * @constructor
 * @param attribute The attribute that will be presented as cell
 * @param aValue The value that will be assigned to cell
 */
class AttributeAsCell<T : Type>(
    attribute: Attribute<out T>,
    aValue: T?
) : Cell {
    private var cellValue: CellValue? = null

    init {
        @Suppress("UNCHECKED_CAST")
        (attribute.writer as AttributeWriter<T>)(aValue, this)
    }

    /**
     * Cell address, it is always 0,0
     */
    override val address = CellAddress(0, 0)
    /**
     * Cell value type
     */
    override val cellType: CellType
        get() = when (cellValue) {
            is CellStringValue -> CellType.STRING
            is CellBooleanValue -> CellType.BOOL
            is CellLongValue -> CellType.LONG
            is CellDoubleValue -> CellType.DOUBLE
            null -> CellType.BLANK
        }

    /**
     * Cell boolean value
     */
    override val booleanValue: Boolean
        get() = when (val v = cellValue) {
            is CellBooleanValue -> v.value
            else -> throw AppDataException(badPlace(this),
                DATA_INCOMPATIBLE_MSG
            )
        }
    /**
     * Cell long value
     */
    override val longValue: Long
        get() = when (val v = cellValue) {
            is CellLongValue -> v.value
            else -> throw AppDataException(badPlace(this),
                DATA_INCOMPATIBLE_MSG
            )
        }
    /**
     * Cell double value
     */
    override val doubleValue: Double
        get() = when (val v = cellValue) {
            is CellDoubleValue -> v.value
            else -> throw AppDataException(badPlace(this),
                DATA_INCOMPATIBLE_MSG
            )
        }
    /**
     * Cell string value
     */
    override val stringValue: String
        get() = when (val v = cellValue) {
            is CellStringValue -> v.value
            else -> throw AppDataException(badPlace(this),
                DATA_INCOMPATIBLE_MSG
            )
        }
    /**
     * Cell row
     */
    override val row: Row
        get() = throw NotImplementedError("Attribute cell has no row")

    /**
     * Cell value as string
     *
     * @return String
     */
    override fun asString(): String {
        return cellValue?.value?.toString() ?: ""
    }

    /**
     * Set cell string value
     *
     * @param value String
     */
    override fun setCellValue(value: String) {
        cellValue = CellStringValue(value)
    }

    /**
     * Set cell boolean value
     *
     * @param value Boolean
     */
    override fun setCellValue(value: Boolean) {
        cellValue = CellBooleanValue(value)
    }

    /**
     * Set cell long value
     *
     * @param value Long
     */
    override fun setCellValue(value: Long) {
        cellValue = CellLongValue(value)
    }

    /**
     * Set cell double value
     *
     * @param value Double
     */
    override fun setCellValue(value: Double) {
        cellValue = CellDoubleValue(value)
    }

    /**
     * Set cell [DateTime] value
     *
     * @param value DateTime
     */
    override fun setCellValue(value: DateTime) {
        cellValue = CellDoubleValue(HSSFDateUtil.getExcelDate(value.toDate()))
    }

    /**
     * Companion object
     */
    companion object {
        /**
         * Error message type is incompatible
         */
        const val DATA_INCOMPATIBLE_MSG = "Data type is incompatible."
    }
}

private sealed class CellValue {
    abstract val value: Any
}

private class CellStringValue(override val value: String) : CellValue()
private class CellBooleanValue(override val value: Boolean) : CellValue()
private class CellLongValue(override val value: Long) : CellValue()
private class CellDoubleValue(override val value: Double) : CellValue()
