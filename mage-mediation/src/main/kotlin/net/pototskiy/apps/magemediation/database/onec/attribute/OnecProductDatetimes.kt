package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime

object OnecProductDatetimes : TypedAttributeTable<DateTime>(
    "onec_product_datetime",
    OnecProducts,
    { datetime("value") }
)

class OnecProductDatetime(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, OnecProductDatetime>(OnecProductDatetimes, DateTime::class)
}