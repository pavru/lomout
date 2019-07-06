class MageCustomerGroup : Document() {
    @Key
    var customer_group_id: Long = 0L
    @Key
    var tax_class_id: Long = 0L

    companion object : DocumentMetadata(MageCustomerGroup::class)
}