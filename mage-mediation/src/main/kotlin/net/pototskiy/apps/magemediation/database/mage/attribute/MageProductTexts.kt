package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object MageProductTexts : TypedAttributeTable<String>("mage_product_text") {
    override val product = reference("product", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = text("value")
}

class MageProductText(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, MageProductText>(MageProductTexts)

    override var product: VersionEntity by MageProduct referencedOn MageProductTexts.product
    override var index by MageProductTexts.index
    override var code by MageProductTexts.code
    override var value by MageProductTexts.value
}