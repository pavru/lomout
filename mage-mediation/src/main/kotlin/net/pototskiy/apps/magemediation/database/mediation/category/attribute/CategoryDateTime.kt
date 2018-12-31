package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime

class CategoryDateTime(id: EntityID<Int>): TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, CategoryDateTime>(CategoryDateTimeTable, DateTime::class)
}