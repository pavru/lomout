package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.jetbrains.exposed.dao.EntityID

object MCategoryIntTable : TypedAttributeTable<Long>(
    "mdtn_cat_int",
    MediumCategories,
    { long("value") }
)

class MCategoryIntAttribute(id: EntityID<Int>) : TypedAttributeEntity<Long>(id) {
    companion object : TypedAttributeEntityClass<Long, MCategoryIntAttribute>(MCategoryIntTable, Long::class)
}