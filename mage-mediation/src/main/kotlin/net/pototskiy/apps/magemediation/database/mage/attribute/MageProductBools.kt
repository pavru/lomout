package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MageProductBools : TypedAttributeTable<Boolean>("mage_product_bool") {
    override val owner = reference("owner", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = bool("value")
}

class MageProductBool(id: EntityID<Int>) : TypedAttributeEntity<Boolean>(id) {
    companion object : TypedAttributeEntityClass<Boolean, MageProductBool>(MageProductBools)

    override var owner: IntEntity by MageProduct.referencedOn(MageProductBools.owner)
    override var index by MageProductBools.index
    override var code by MageProductBools.code
    override var value by MageProductBools.value
}
