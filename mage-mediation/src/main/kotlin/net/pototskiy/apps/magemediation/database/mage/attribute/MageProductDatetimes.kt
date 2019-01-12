package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime

object MageProductDatetimes : TypedAttributeTable<DateTime>(
    "mage_product_datetime",
    MageProducts,
    { datetime("value") }
)

class MageProductDatetime(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, MageProductDatetime>(MageProductDatetimes, DateTime::class)
}
