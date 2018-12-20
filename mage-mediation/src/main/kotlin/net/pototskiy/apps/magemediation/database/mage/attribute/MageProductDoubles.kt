package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import kotlin.math.abs

object MageProductDoubles : DoubleAttribute("mage_product_double") {
    override val product = reference("product", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value=double("value")
}

class MageProductDouble(id: EntityID<Int>) : DoubleAttributeEntity(id) {
    companion object : DoubleAttributeEntityClass<MageProductDouble>(MageProductDoubles)

    override var product:VersionEntity by MageProduct referencedOn MageProductDoubles.product
    override var index by MageProductDoubles.index
    override var code by MageProductDoubles.code
    override var value by MageProductDoubles.value
}
