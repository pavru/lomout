package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID

object MageProductDoubles : TypedAttributeTable<Double>(
    "mage_product_double",
    MageProducts,
    { double("value") }
)

class MageProductDouble(id: EntityID<Int>) : TypedAttributeEntity<Double>(id) {
    companion object : TypedAttributeEntityClass<Double, MageProductDouble>(MageProductDoubles, Double::class)
}
