package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import org.jetbrains.exposed.dao.EntityID

object MageCatDoubles : TypedAttributeTable<Double>(
    "mage_cat_double",
    MageCategories,
    { double("value") }
)

class MageCatDouble(id: EntityID<Int>) : TypedAttributeEntity<Double>(id) {
    companion object : TypedAttributeEntityClass<Double, MageCatDouble>(MageCatDoubles, Double::class)
}