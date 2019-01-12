package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID

object MageProductVarchars : TypedAttributeTable<String>(
    "mage_product_varchar",
    MageProducts,
    { varchar("value", 800) }
)

class MageProductVarchar(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, MageProductVarchar>(MageProductVarchars, String::class)
}