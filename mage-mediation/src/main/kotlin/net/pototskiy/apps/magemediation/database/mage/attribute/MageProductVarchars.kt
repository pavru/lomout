package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object MageProductVarchars : VarcharAttribute("mage_product_varchar") {
    override val product = reference("product", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = varchar("value", 800)
}

class MageProductVarchar(id: EntityID<Int>) : VarcharAttributeEntity(id) {
    companion object : VarcharAttributeEntityClass<MageProductVarchar>(MageProductVarchars)

    override var product:VersionEntity by MageProduct referencedOn MageProductVarchars.product
    override var index by MageProductVarchars.index
    override var code by MageProductVarchars.code
    override var value by MageProductVarchars.value
}