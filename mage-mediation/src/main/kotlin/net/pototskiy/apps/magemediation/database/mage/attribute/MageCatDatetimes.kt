package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime

object MageCatDatetimes : TypedAttributeTable<DateTime>(
    "mage_cat_datetime",
    MageCategories,
    { datetime("value") }
)

class MageCatDatetime(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, MageCatDatetime>(MageCatDatetimes, DateTime::class)
}