package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID

object OnecProductDoubles : TypedAttributeTable<Double>(
    "onec_product_double",
    OnecProducts,
    { double("value") }
)

class OnecProductDouble(id: EntityID<Int>) : TypedAttributeEntity<Double>(id) {
    companion object : TypedAttributeEntityClass<Double, OnecProductDouble>(OnecProductDoubles, Double::class)
}