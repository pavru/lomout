package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID

abstract class AdvPriceEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var sku: String
    abstract var tierPriceWebsite: String
    abstract var tierPriceCustomerGroup: String
    abstract var tierPriceQty: Double
    abstract var tierPrice: Double
    abstract var tierPriceValueType: String
}