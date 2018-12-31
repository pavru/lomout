package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID

object OnecProductBools : TypedAttributeTable<Boolean>(
    "onec_product_bool",
    OnecProducts,
    { bool("value") }
)

class OnecProductBool(id: EntityID<Int>) : TypedAttributeEntity<Boolean>(id) {
    companion object : TypedAttributeEntityClass<Boolean, OnecProductBool>(OnecProductBools, Boolean::class)
}
