class MageStockSource : Document() {
    @Key
    var source_code: String = ""
    @Key
    var sku: String = ""
    var status: Boolean = false
    var quantity: Double = 0.0

    companion object : DocumentMetadata(MageStockSource::class)
}