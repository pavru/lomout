package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.jetbrains.exposed.dao.EntityID

object MCategoryDoubleTable : TypedAttributeTable<Double>(
    "mdtn_cat_double",
    MediumCategories,
    { double("value") }
)

class MCategoryDoubleAttribute(id: EntityID<Int>) : TypedAttributeEntity<Double>(id) {
    companion object : TypedAttributeEntityClass<Double, MCategoryDoubleAttribute>(MCategoryDoubleTable, Double::class)
}