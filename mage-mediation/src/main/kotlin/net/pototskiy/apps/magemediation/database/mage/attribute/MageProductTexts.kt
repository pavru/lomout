package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object MageProductTexts : TextAttribute("mage_product_text") {
    override val product = reference("product", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = text("value")
}

class MageProductText(id: EntityID<Int>) : TextAttributeEntity(id) {
    companion object : TextAttributeEntityClass<MageProductText>(MageProductTexts)

    override var product:VersionEntity by MageProduct referencedOn MageProductTexts.product
    override var index by MageProductTexts.index
    override var code by MageProductTexts.code
    override var value by MageProductTexts.value
}