package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime
import org.joda.time.Duration
import kotlin.math.abs

object MageProductDatetimes : DatetimeAttribute("mage_product_datetime") {
    override val product = reference("product", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = datetime("value")
}

class MageProductDatetime(id: EntityID<Int>) : DatetimeAttributeEntity(id) {
    companion object : DatetimeAttributeEntityClass<MageProductDatetime>(MageProductDatetimes)

    override var product: VersionEntity by MageProduct referencedOn MageProductDatetimes.product
    override var index by MageProductDatetimes.index
    override var code by MageProductDatetimes.code
    override var value by MageProductDatetimes.value
}
