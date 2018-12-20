package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object OnecProductDatetimes : DatetimeAttribute("onec_product_datetime") {
    override val product = reference("product", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = datetime("value")
}

class OnecProductDatetime(id: EntityID<Int>) : DatetimeAttributeEntity(id) {
    companion object : DatetimeAttributeEntityClass<OnecProductDatetime>(OnecProductDatetimes)

    override var product:VersionEntity by OnecProduct referencedOn OnecProductDatetimes.product
    override var index by OnecProductDatetimes.index
    override var code by OnecProductDatetimes.code
    override var value by OnecProductDatetimes.value
}