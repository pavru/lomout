package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.jetbrains.exposed.dao.EntityID

object MCategoryVarcharTable : TypedAttributeTable<String>(
    "mdtn_cat_varchar",
    MediumCategories,
    { varchar("value", 300) }
)

class MCategoryVarcharAttribute(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, MCategoryVarcharAttribute>(MCategoryVarcharTable, String::class)
}