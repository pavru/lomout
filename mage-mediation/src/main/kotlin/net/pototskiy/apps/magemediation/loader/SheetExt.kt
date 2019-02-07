package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.config.data.Entity
import net.pototskiy.apps.magemediation.api.config.data.Field
import net.pototskiy.apps.magemediation.api.config.data.FieldCollection
import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.AttributeStringType
import net.pototskiy.apps.magemediation.source.Sheet

fun Sheet.sourceFields(row: Int, entity: Entity): FieldCollection {
    val rowData = this[row]
        ?: throw LoaderException("There is no row that is defined as headers row")
    return rowData.mapIndexed { i, cell ->
        if (cell == null) {
            throw LoaderStopException("Header cell can not be read in the position ${i + 1}")
        }
        val attr = entity.attributes.find { it.name == cell.asString() }
            ?: Attribute(
                cell.asString(),
                AttributeStringType(false),
                false,
                true,
                true,
                null
            )
        Field(cell.asString(), cell.address.column, null, null, null) to attr
    }.toMap().let { FieldCollection(it) }
}
