package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import org.jetbrains.exposed.dao.EntityID

class CategoryInt(id: EntityID<Int>) : TypedAttributeEntity<Long>(id) {
    companion object : TypedAttributeEntityClass<Long, CategoryInt>(CategoryIntTable,Long::class)
}