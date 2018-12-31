package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.config.dataset.Field
import net.pototskiy.apps.magemediation.config.dataset.ListDefinition
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellType
import java.text.NumberFormat
import java.util.*

class StringConverter(
    private val cell: Cell,
    private val field: Field
) {
    fun convert(): String {
        val format = NumberFormat.getInstance().apply {
            isGroupingUsed = false
        }
        return when(cell.cellType) {
            CellType.INT -> format.format(cell.intValue)
            CellType.DOUBLE -> doubleToString(cell.doubleValue)
            CellType.BOOL -> cell.booleanValue.toString()
            CellType.STRING -> cell.stringValue
        }
    }

    fun convertList(): List<String> {
        val format = NumberFormat.getInstance().apply {
            isGroupingUsed = false
        }
        return when(cell.cellType){
            CellType.INT -> listOf(format.format(cell.intValue))
            CellType.DOUBLE -> listOf(doubleToString(cell.doubleValue))
            CellType.BOOL -> listOf(cell.booleanValue.toString())
            CellType.STRING -> ValueListParser(
                cell.stringValue,
                field.typeDefinitions.findLast { it is ListDefinition } as ListDefinition
            )
                .parse()
        }
    }

    private fun doubleToString(value: Double):String {
        val format = field.locale?.let {
            val code = it.split("_")
            NumberFormat.getInstance(Locale(code[0],code[1]))
        } ?: NumberFormat.getInstance()
        format.isGroupingUsed = false
        return format.format(value)
    }
}