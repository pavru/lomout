package net.pototskiy.apps.lomout.api.source.workbook

import org.joda.time.DateTime

/**
 * Workbook cell interface
 *
 * @property address CellAddress
 * @property cellType CellType
 * @property booleanValue Boolean
 * @property longValue Long
 * @property doubleValue Double
 * @property stringValue String
 * @property value Any?
 * @property row Row
 */
interface Cell {
    /**
     * Cell address (row,column)
     */
    val address: CellAddress
    /**
     * Cell value type
     */
    val cellType: CellType
    /**
     * Cell boolean value
     */
    val booleanValue: Boolean
    /**
     * Cell long value
     */
    val longValue: Long
    /**
     * Cell double value
     */
    val doubleValue: Double
    /**
     * Cell string value
     */
    val stringValue: String
    /**
     * Cell value
     */
    val value: Any?
        get() {
            return when (cellType) {
                CellType.LONG -> longValue
                CellType.DOUBLE -> doubleValue
                CellType.BOOL -> booleanValue
                CellType.STRING -> stringValue
                CellType.BLANK -> null
            }
        }

    /**
     * Set string cell value
     *
     * @param value String
     */
    fun setCellValue(value: String)

    /**
     * Set cell boolean value
     *
     * @param value Boolean
     */
    fun setCellValue(value: Boolean)

    /**
     * Set cell long value
     * @param value Long
     */
    fun setCellValue(value: Long)

    /**
     * Set cell double value
     *
     * @param value Double
     */
    fun setCellValue(value: Double)

    /**
     * Set cell [DateTime] value
     *
     * @param value DateTime
     */
    fun setCellValue(value: DateTime)

    /**
     * Cell row
     */
    val row: Row

    /**
     * Cell value as string
     *
     * @return String
     */
    fun asString(): String
}
