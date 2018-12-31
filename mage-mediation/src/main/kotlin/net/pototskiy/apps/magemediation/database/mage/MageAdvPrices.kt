package net.pototskiy.apps.magemediation.database.mage

import org.jetbrains.exposed.dao.EntityID

object MageAdvPrices : AdvPriceTable("mage_adv_price")

class MageAdvPrice(id: EntityID<Int>) : AdvPriceEntity(id) {
    companion object : AdvPriceEntityClass(MageAdvPrices);

    override var sku by MageAdvPrices.sku
    override var tierPriceWebsite by MageAdvPrices.tierPriceWebsite
    override var tierPriceCustomerGroup by MageAdvPrices.tierPriceCustomerGroup
    override var tierPriceQty by MageAdvPrices.tierPriceQty
    override var tierPrice by MageAdvPrices.tierPrice
    override var tierPriceValueType by MageAdvPrices.tierPriceValueType
}
