package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.jetbrains.exposed.dao.EntityID

object MCategoryBoolTable : TypedAttributeTable<Boolean>(
    "medium_cat_bool",
    MediumCategories,
    { bool("value") }
)

class MCategoryBoolAttribute(id: EntityID<Int>) : TypedAttributeEntity<Boolean>(id) {
    companion object : TypedAttributeEntityClass<Boolean, MCategoryBoolAttribute>(MCategoryBoolTable, Boolean::class)
}
