package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object OnecProductInts : IntAttribute("onec_product_int") {
    override val product = reference("product", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = long("value")
}

class OnecProductInt(id: EntityID<Int>) : IntAttributeEntity(id) {
    companion object : IntAttributeEntityClass<OnecProductInt>(OnecProductInts)

    override var product:VersionEntity by OnecProduct referencedOn OnecProductInts.product
    override var index by OnecProductInts.index
    override var code by OnecProductInts.code
    override var value by OnecProductInts.value
}