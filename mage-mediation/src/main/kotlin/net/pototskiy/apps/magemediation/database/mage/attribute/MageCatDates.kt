package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime

object MageCatDates : TypedAttributeTable<DateTime>(
    "mage_cat_date",
    MageCategories,
    { date("value") }
)

class MageCatDate(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, MageCatDate>(MageCatDates, DateTime::class)
}