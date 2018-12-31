package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import org.jetbrains.exposed.dao.EntityID

class CategoryBool(id: EntityID<Int>) : TypedAttributeEntity<Boolean>(id) {
    companion object : TypedAttributeEntityClass<Boolean, CategoryBool>(CategoryBoolTable, Boolean::class)
}
