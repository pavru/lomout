package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MageProductDoubles : TypedAttributeTable<Double>("mage_product_double") {
    override val owner = reference("product", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = double("value")
}

class MageProductDouble(id: EntityID<Int>) : TypedAttributeEntity<Double>(id) {
    companion object : TypedAttributeEntityClass<Double, MageProductDouble>(MageProductDoubles)

    override var owner: IntEntity by MageProduct referencedOn MageProductDoubles.owner
    override var index by MageProductDoubles.index
    override var code by MageProductDoubles.code
    override var value by MageProductDoubles.value
}
