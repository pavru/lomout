package net.pototskiy.apps.magemediation.api.source.workbook

import org.joda.time.DateTime

interface Cell {
    val address: CellAddress
    val cellType: CellType
    val booleanValue: Boolean
    val longValue: Long
    val doubleValue: Double
    val stringValue: String
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

    fun setCellValue(value: String)
    fun setCellValue(value: Boolean)
    fun setCellValue(value: Long)
    fun setCellValue(value: Double)
    fun setCellValue(value: DateTime)

    val row: Row

    fun asString(): String
}
