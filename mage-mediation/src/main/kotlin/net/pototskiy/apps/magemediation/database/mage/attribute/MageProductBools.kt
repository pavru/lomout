package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID

object MageProductBools : TypedAttributeTable<Boolean>(
    "mage_product_bool",
    MageProducts,
    { bool("value") }
)

class MageProductBool(id: EntityID<Int>) : TypedAttributeEntity<Boolean>(id) {
    companion object : TypedAttributeEntityClass<Boolean, MageProductBool>(MageProductBools, Boolean::class)
}
