package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataTable

abstract class AdvPriceTable(name:String): SourceDataTable(name) {
    val sku = varchar("sku", 300).index()
    val tierPriceWebsite = varchar("tier_price_website", 200).index()
    val tierPriceCustomerGroup = varchar("tier_price_customer_group", 200).index()
    val tierPriceQty = double("tier_price_qty")
    val tierPrice = double("tier_price")
    val tierPriceValueType = varchar("tier_price_value_type", 200)

    init {
        uniqueIndex(sku, tierPriceWebsite, tierPriceCustomerGroup)
    }
}