package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID

object OnecProductInts : TypedAttributeTable<Long>(
    "onec_product_int",
    OnecProducts,
    { long("value") }
)

class OnecProductInt(id: EntityID<Int>) : TypedAttributeEntity<Long>(id) {
    companion object : TypedAttributeEntityClass<Long, OnecProductInt>(OnecProductInts, Long::class)
}