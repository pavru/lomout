package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object OnecProductDates : DateAttribute("onec_product_date") {
    override val product = reference("product", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = date("value")
}

class OnecProductDate(id: EntityID<Int>) : DateAttributeEntity(id) {
    companion object : DateAttributeEntityClass<OnecProductDate>(OnecProductDates)

    override var product: VersionEntity by OnecProduct referencedOn OnecProductDates.product
    override var index by OnecProductDates.index
    override var code by OnecProductDates.code
    override var value by OnecProductDates.value
}