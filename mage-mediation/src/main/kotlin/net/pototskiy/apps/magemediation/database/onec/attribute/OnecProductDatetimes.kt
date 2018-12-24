package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime

object OnecProductDatetimes : TypedAttributeTable<DateTime>("onec_product_datetime") {
    override val owner = reference("product", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = datetime("value")
}

class OnecProductDatetime(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, OnecProductDatetime>(OnecProductDatetimes)

    override var owner: IntEntity by OnecProduct referencedOn OnecProductDatetimes.owner
    override var index by OnecProductDatetimes.index
    override var code by OnecProductDatetimes.code
    override var value by OnecProductDatetimes.value
}