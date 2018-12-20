package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.attribute.*
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object OnecProductBools : BoolAttribute("onec_product_bool") {
    override val product = reference("product", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = bool("value")
}

class OnecProductBool(id: EntityID<Int>) : BoolAttributeEntity(id) {
    companion object : BoolAttributeEntityClass<OnecProductBool>(OnecProductBools)

    override var product:VersionEntity by OnecProduct referencedOn OnecProductBools.product
    override var index by OnecProductBools.index
    override var code by OnecProductBools.code
    override var value by OnecProductBools.value
}
