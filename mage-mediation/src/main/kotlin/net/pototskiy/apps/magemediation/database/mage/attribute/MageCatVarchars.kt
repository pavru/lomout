package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import org.jetbrains.exposed.dao.EntityID

object MageCatVarchars : TypedAttributeTable<String>(
    "mage_cat_varchar",
    MageCategories,
    { varchar("value", 800) }
)

class MageCatVarchar(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, MageCatVarchar>(MageCatVarchars, String::class)
}