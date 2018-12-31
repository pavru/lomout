package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import org.jetbrains.exposed.dao.EntityID

object MageCatInts : TypedAttributeTable<Long>(
    "mage_cat_int",
    MageCategories,
    { long("value") }
)

class MageCatInt(id: EntityID<Int>) : TypedAttributeEntity<Long>(id) {
    companion object : TypedAttributeEntityClass<Long, MageCatInt>(MageCatInts, Long::class)
}