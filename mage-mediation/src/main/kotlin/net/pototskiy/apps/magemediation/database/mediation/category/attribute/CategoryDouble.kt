package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import org.jetbrains.exposed.dao.EntityID

class CategoryDouble(id: EntityID<Int>): TypedAttributeEntity<Double>(id) {
    companion object : TypedAttributeEntityClass<Double, CategoryDouble>(CategoryDoubleTable, Double::class)
}