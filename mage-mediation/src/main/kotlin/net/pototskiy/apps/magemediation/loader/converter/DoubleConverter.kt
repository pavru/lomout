package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.FieldConfiguration
import net.pototskiy.apps.magemediation.loader.LoaderException
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellType
import java.text.NumberFormat
import java.text.ParseException


class DoubleConverter(
    private val cell: Cell,
    private val field: FieldConfiguration
) {
    fun convert(): Double = when (cell.cellType) {
        CellType.INT -> cell.intValue.toDouble()
        CellType.DOUBLE -> cell.doubleValue
        CellType.BOOL -> if (cell.booleanValue) 1.0 else 0.0
        CellType.STRING -> stringToDouble(cell.stringValue)
    }

    fun convertList(): List<Double> {
        return when (cell.cellType) {
            CellType.INT -> listOf(cell.intValue.toDouble())
            CellType.DOUBLE -> listOf(cell.doubleValue)
            CellType.BOOL -> listOf(if (cell.booleanValue) 1.0 else 0.0)
            CellType.STRING -> {
                ValueListParser(
                    cell.stringValue,
                    field.type
                )
                    .parse()
                    .map { stringToDouble(it) }
            }
        }
    }

    private fun stringToDouble(value: String): Double {
        val format = NumberFormat.getInstance(field.type.getLocaleObject())
        try {
            return format.parse(value).toDouble()
        } catch (e: ParseException) {
            throw LoaderException("Field<${field.name}>, string can not be converted to double")
        }
    }
}