package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.config.excel.Field
import net.pototskiy.apps.magemediation.config.excel.ListDefinition
import net.pototskiy.apps.magemediation.loader.LoaderException
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellType
import java.text.NumberFormat
import java.text.ParseException
import java.util.*


class DoubleConverter(
    private val cell: Cell,
    private val field: Field
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
                    field.typeDefinitions.findLast { it is ListDefinition } as ListDefinition
                )
                    .parse()
                    .map { stringToDouble(it) }
            }
        }
    }

    private fun stringToDouble(value: String): Double {
        val format = field.locale?.let {
            val code = field.locale?.split("_") as List<String>
            NumberFormat.getInstance(Locale(code[0], code[1]))
        } ?: NumberFormat.getInstance()
        try {
            return format.parse(value).toDouble()
        } catch (e: ParseException) {
            throw LoaderException("Field<${field.name}>, string can not be converted to double")
        }
    }
}