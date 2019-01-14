package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime

object MCategoryDateTimeTable : TypedAttributeTable<DateTime>(
    "mdtn_cat_datetime",
    MediumCategories,
    { datetime("value") }
)

class MCategoryDateTimeAttribute(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, MCategoryDateTimeAttribute>(MCategoryDateTimeTable, DateTime::class)
}