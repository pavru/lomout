package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object OnecProductDoubles : DoubleAttribute("onec_product_double") {
    override val product = reference("product", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value=double("value")
}

class OnecProductDouble(id: EntityID<Int>) : DoubleAttributeEntity(id) {
    companion object : DoubleAttributeEntityClass<OnecProductDouble>(OnecProductDoubles)

    override var product:VersionEntity by OnecProduct referencedOn OnecProductDoubles.product
    override var index by OnecProductDoubles.index
    override var code by OnecProductDoubles.code
    override var value by OnecProductDoubles.value
}