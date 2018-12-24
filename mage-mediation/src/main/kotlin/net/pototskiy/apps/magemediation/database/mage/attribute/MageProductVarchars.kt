package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object MageProductVarchars : TypedAttributeTable<String>("mage_product_varchar") {
    override val owner = reference("owner", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = varchar("value", 800)
}

class MageProductVarchar(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, MageProductVarchar>(MageProductVarchars)

    override var owner: VersionEntity by MageProduct referencedOn MageProductVarchars.owner
    override var index by MageProductVarchars.index
    override var code by MageProductVarchars.code
    override var value by MageProductVarchars.value
}