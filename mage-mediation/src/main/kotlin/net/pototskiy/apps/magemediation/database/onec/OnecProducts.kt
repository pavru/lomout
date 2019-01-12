package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.api.database.onec.ProductEntity
import net.pototskiy.apps.magemediation.api.database.onec.ProductEntityClass
import net.pototskiy.apps.magemediation.api.database.onec.ProductTable
import net.pototskiy.apps.magemediation.database.onec.attribute.*
import org.jetbrains.exposed.dao.EntityID

object OnecProducts : ProductTable("onec_product")

class OnecProduct(id: EntityID<Int>) : ProductEntity(id) {
    companion object : ProductEntityClass(
        OnecProducts,
        null,
        OnecProductBool.Companion,
        OnecProductDate.Companion,
        OnecProductDatetime.Companion,
        OnecProductDouble.Companion,
        OnecProductInt.Companion,
        OnecProductText.Companion,
        OnecProductVarchar.Companion
    )

    override var sku by OnecProducts.sku
}
