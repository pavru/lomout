package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import org.jetbrains.exposed.dao.EntityID

object MageCatBools : TypedAttributeTable<Boolean>(
    "mage_cat_bool",
    MageCategories,
    { bool("value") }
)

class MageCatBool(id: EntityID<Int>) : TypedAttributeEntity<Boolean>(id) {
    companion object : TypedAttributeEntityClass<Boolean, MageCatBool>(MageCatBools, Boolean::class)
}