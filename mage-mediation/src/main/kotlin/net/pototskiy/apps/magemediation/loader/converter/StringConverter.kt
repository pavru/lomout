package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.FieldConfiguration
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellType
import java.text.NumberFormat

class StringConverter(
    private val cell: Cell,
    private val field: FieldConfiguration
) {
    fun convert(): String {
        return when (cell.cellType) {
            CellType.INT -> intToString(cell.intValue)
            CellType.DOUBLE -> doubleToString(cell.doubleValue)
            CellType.BOOL -> cell.booleanValue.toString()
            CellType.STRING -> cell.stringValue
        }
    }

    fun convertList(): List<String> {
        return when (cell.cellType) {
            CellType.INT -> listOf(intToString(cell.intValue))
            CellType.DOUBLE -> listOf(doubleToString(cell.doubleValue))
            CellType.BOOL -> listOf(cell.booleanValue.toString())
            CellType.STRING -> ValueListParser(
                cell.stringValue,
                field.type
            )
                .parse()
        }
    }

    private fun doubleToString(value: Double): String {
        val format = NumberFormat.getInstance(field.type.getLocaleObject())
        format.isGroupingUsed = false
        return format.format(value)
    }

    private fun intToString(value: Long): String {
        val format = NumberFormat.getIntegerInstance(field.type.getLocaleObject())
        format.isGroupingUsed = false
        return format.format(value)
    }
}