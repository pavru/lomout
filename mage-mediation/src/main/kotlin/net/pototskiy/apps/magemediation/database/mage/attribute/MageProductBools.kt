package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.attribute.BoolAttribute
import net.pototskiy.apps.magemediation.database.attribute.BoolAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.BoolAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object MageProductBools : BoolAttribute("mage_product_bool") {
    override val product = reference("product", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = bool("value")
}

class MageProductBool(id: EntityID<Int>) : BoolAttributeEntity(id) {
    companion object : BoolAttributeEntityClass<MageProductBool>(MageProductBools)

    override var product: VersionEntity by MageProduct.referencedOn(MageProductBools.product)
    override var index by MageProductBools.index
    override var code by MageProductBools.code
    override var value by MageProductBools.value
}
