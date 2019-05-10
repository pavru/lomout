package net.pototskiy.apps.lomout.api.source.nested

import net.pototskiy.apps.lomout.api.AppCellDataException
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.joda.time.DateTime

/**
 * Attribute workbook cell
 *
 * @property address CellAddress
 * @property backingValue String
 * @property row NestedAttributeRow
 * @property cellType CellType
 * @property booleanValue Boolean
 * @property longValue Long
 * @property doubleValue Double
 * @property stringValue String
 * @constructor
 * @param address CellAddress The cell address
 * @param backingValue String The cell value
 * @param row NestedAttributeRow The cell row
 */
class NestedAttributeCell(
    override val address: CellAddress,
    private var backingValue: String,
    override val row: NestedAttributeRow
) : Cell {

    /**
     * Get cell value as string
     *
     * @return String
     */
    override fun asString(): String {
        return stringValue
    }

    /**
     * Cell value type, always is [CellType.STRING]
     */
    override val cellType: CellType = CellType.STRING
    /**
     * Cell boolean value
     */
    override val booleanValue: Boolean
        get() = throwNotSupportCellType()
    /**
     * Cell long value
     */
    override val longValue: Long
        get() = throwNotSupportCellType()
    /**
     * Cell double value
     */
    override val doubleValue: Double
        get() = throwNotSupportCellType()

    private fun throwNotSupportCellType(): Nothing {
        throw AppCellDataException("${NestedAttributeCell::class.simpleName} supports only string type value")
    }

    /**
     * Cell string value
     */
    override val stringValue: String
        get() = backingValue

    /**
     * Set cell string value
     *
     * @param value String
     */
    override fun setCellValue(value: String) {
        backingValue = value
    }

    /**
     * Set cell boolean value
     *
     * @param value Boolean
     * @return Nothing
     */
    override fun setCellValue(value: Boolean) = throwNotSupportCellType()

    /**
     * Set cell long value
     *
     * @param value Long
     * @return Nothing
     */
    override fun setCellValue(value: Long) = throwNotSupportCellType()

    /**
     * Set cell double value
     *
     * @param value Double
     * @return Nothing
     */
    override fun setCellValue(value: Double) = throwNotSupportCellType()

    /**
     * Set cell [DateTime] value
     *
     * @param value DateTime
     * @return Nothing
     */
    override fun setCellValue(value: DateTime) = throwNotSupportCellType()
}
