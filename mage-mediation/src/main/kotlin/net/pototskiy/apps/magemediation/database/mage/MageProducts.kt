package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.mage.attribute.*
import org.jetbrains.exposed.dao.EntityID

object MageProducts : ProductTable("mage_product")


class MageProduct(id: EntityID<Int>) : ProductEntity(id) {
    companion object : ProductEntityClass(
        MageProducts,
        null,
        MageProductBool.Companion,
        MageProductDate.Companion,
        MageProductDatetime.Companion,
        MageProductDouble.Companion,
        MageProductInt.Companion,
        MageProductText.Companion,
        MageProductVarchar.Companion
    );

    override var sku by MageProducts.sku
}
