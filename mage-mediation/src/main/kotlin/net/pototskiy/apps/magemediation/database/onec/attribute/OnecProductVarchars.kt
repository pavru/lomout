package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object OnecProductVarchars : VarcharAttribute("onec_product_varchar") {
    override val product = reference("product", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = varchar("value", 800)
}

class OnecProductVarchar(id: EntityID<Int>) : VarcharAttributeEntity(id) {
    companion object : VarcharAttributeEntityClass<OnecProductVarchar>(OnecProductVarchars)

    override var product:VersionEntity by OnecProduct referencedOn OnecProductVarchars.product
    override var index by OnecProductVarchars.index
    override var code by OnecProductVarchars.code
    override var value by OnecProductVarchars.value
}