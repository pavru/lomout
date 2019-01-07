package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.FieldConfiguration
import net.pototskiy.apps.magemediation.loader.LoaderException
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellType

class BooleanConverter(
    private val cell: Cell,
    private val field: FieldConfiguration
) {
    fun convert(): Boolean {
        return when (cell.cellType) {
            CellType.BOOL -> cell.booleanValue
            CellType.STRING -> stringToBoolean(cell.stringValue)
            CellType.INT -> cell.intValue != 0L
            CellType.DOUBLE -> cell.doubleValue != 0.0
        }
    }

    private fun stringToBoolean(value: String): Boolean {
        val v = value.toLowerCase().trim()
        return if (v in stringBoolean)
            v in stringBooleanTrue
        else
            throw LoaderException("Field<${field.name}>, string can not converted to boolean")
    }

    fun convertList(): List<Boolean> {
        return when (cell.cellType) {
            CellType.INT -> listOf(cell.intValue != 0L)
            CellType.DOUBLE -> listOf(cell.doubleValue != 0.0)
            CellType.BOOL -> listOf(cell.booleanValue)
            CellType.STRING -> {
                ValueListParser(
                    cell.stringValue,
                    field.type
                )
                    .parse()
                    .map { stringToBoolean(it) }
            }
        }
    }

    companion object {
        val stringBoolean = listOf("1", "yes", "true", "0", "no", "false")
        val stringBooleanTrue = listOf("1", "yes", "true")
    }
}
