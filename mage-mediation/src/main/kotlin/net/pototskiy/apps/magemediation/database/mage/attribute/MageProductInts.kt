package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import kotlin.math.abs

object MageProductInts : IntAttribute("mage_product_int") {
    override val product = reference("product", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = long("value")
}

class MageProductInt(id: EntityID<Int>) : IntAttributeEntity(id) {
    companion object : IntAttributeEntityClass<MageProductInt>(MageProductInts)

    override var product:VersionEntity by MageProduct referencedOn MageProductInts.product
    override var index by MageProductInts.index
    override var code by MageProductInts.code
    override var value by MageProductInts.value
}
