package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime

object MageProductDatetimes : TypedAttributeTable<DateTime>("mage_product_datetime") {
    override val owner = reference("owner", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = datetime("value")
}

class MageProductDatetime(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, MageProductDatetime>(MageProductDatetimes)

    override var owner: IntEntity by MageProduct referencedOn MageProductDatetimes.owner
    override var index by MageProductDatetimes.index
    override var code by MageProductDatetimes.code
    override var value by MageProductDatetimes.value
}
