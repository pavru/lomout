package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.config.dataset.Field
import net.pototskiy.apps.magemediation.config.dataset.ListDefinition
import net.pototskiy.apps.magemediation.floorToLong
import net.pototskiy.apps.magemediation.fraction
import net.pototskiy.apps.magemediation.loader.LoaderException
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellType

class IntegerConverter(
    private val cell: Cell,
    private val field: Field
) {
    fun convert(): Long = when (cell.cellType) {
        CellType.INT -> cell.intValue
        CellType.DOUBLE -> doubleToInt(cell.doubleValue)
        CellType.BOOL -> if (cell.booleanValue) 1 else 0
        CellType.STRING ->
            cell.stringValue.toLongOrNull()
                ?: throw LoaderException("Field<${field.name}>, string can not converted to int")
    }

    private fun doubleToInt(value: Double): Long {
        return if (value.fraction == 0.0)
            value.floorToLong()
        else
            throw LoaderException("Field<${field.name}>, double can not converted to int")
    }

    fun convertList(): List<Long> = when (cell.cellType) {
        CellType.INT -> listOf(cell.intValue)
        CellType.DOUBLE -> listOf(doubleToInt(cell.doubleValue))
        CellType.BOOL -> listOf(if (cell.booleanValue) 1L else 0L)
        CellType.STRING -> ValueListParser(
            cell.stringValue,
            (field.typeDefinitions.findLast { it is ListDefinition } as ListDefinition)
        )
            .parse()
            .map {
                it.toLongOrNull()
                    ?: throw LoaderException("Field<${field.name}>, string can not converted to int")
            }
    }
}
