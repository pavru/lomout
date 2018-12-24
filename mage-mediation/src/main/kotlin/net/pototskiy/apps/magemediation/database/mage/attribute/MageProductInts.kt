package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object MageProductInts : TypedAttributeTable<Long>("mage_product_int") {
    override val owner = reference("owner", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = long("value")
}

class MageProductInt(id: EntityID<Int>) : TypedAttributeEntity<Long>(id) {
    companion object : TypedAttributeEntityClass<Long, MageProductInt>(MageProductInts)

    override var owner: VersionEntity by MageProduct referencedOn MageProductInts.owner
    override var index by MageProductInts.index
    override var code by MageProductInts.code
    override var value by MageProductInts.value
}
