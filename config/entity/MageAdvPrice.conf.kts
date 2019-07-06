class MageAdvPrice : Document() {
    @Key
    var sku: String = ""
    @Key
    var tier_price_website: String = ""
    @Key
    var tier_price_customer_group: String = ""
    var tier_price_qty: Double = 0.0
    var tier_price: Double = 0.0

    companion object : DocumentMetadata(MageAdvPrice::class)
}