package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID

object OnecProductVarchars : TypedAttributeTable<String>(
    "onec_product_varchar",
    OnecProducts,
    { varchar("value", 800) }
)

class OnecProductVarchar(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, OnecProductVarchar>(OnecProductVarchars, String::class)
}