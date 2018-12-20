package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.MageProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime
import org.joda.time.Duration
import kotlin.math.abs
import kotlin.math.sign

object MageProductDates : DateAttribute("mage_product_date") {
    override val product = reference("product", MageProducts, onDelete = ReferenceOption.CASCADE)
    override val value = date("value")
}

class MageProductDate(id: EntityID<Int>) : DateAttributeEntity(id) {
    companion object : DateAttributeEntityClass<MageProductDate>(MageProductDates)

    override var product: VersionEntity by MageProduct referencedOn MageProductDates.product
    override var index by MageProductDates.index
    override var code by MageProductDates.code
    override var value by MageProductDates.value
}
