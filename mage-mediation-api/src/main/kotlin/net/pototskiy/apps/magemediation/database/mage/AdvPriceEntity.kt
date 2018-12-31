package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.getDelegate
import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

abstract class AdvPriceEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var sku: String
    abstract var tierPriceWebsite: String
    abstract var tierPriceCustomerGroup: String
    abstract var tierPriceQty: Double
    abstract var tierPrice: Double
    abstract var tierPriceValueType: String

    @Suppress("UNCHECKED_CAST")
    final override fun isNotEqual(data: Map<String, Any?>): Boolean {
        val tierPriceQtyColumn = getDelegate(this, ::tierPriceQty) as Column<Double>
        val tierPriceColumn = getDelegate(this, ::tierPrice) as Column<Double>
        val tierPriceValueTypeColumn = getDelegate(this, ::tierPriceValueType) as Column<String>

        return tierPriceQty != data[tierPriceQtyColumn.name] as Double
                || tierPrice != data[tierPriceColumn.name] as Double
                || tierPriceValueType != data[tierPriceValueTypeColumn.name] as String
    }

    @Suppress("UNCHECKED_CAST")
    final override fun updateEntity(data: Map<String, Any?>) {
        val tierPriceQtyColumn = getDelegate(this, ::tierPriceQty) as Column<Double>
        val tierPriceColumn = getDelegate(this, ::tierPrice) as Column<Double>
        val tierPriceValueTypeColumn = getDelegate(this, ::tierPriceValueType) as Column<String>

        transaction {
            tierPriceQty != data[tierPriceQtyColumn.name] as Double
            tierPrice != data[tierPriceColumn.name] as Double
            tierPriceValueType != data[tierPriceValueTypeColumn.name] as String
        }
        wasUpdated()
    }

    @Suppress("UNCHECKED_CAST")
    final override fun setEntityData(data: Map<String, Any?>) {
        val skuColumn = getDelegate(this, ::sku) as Column<String>
        val tierPriceWebsiteColumn = getDelegate(this, ::tierPriceWebsite) as Column<String>
        val tierPriceCustomerGroupColumn = getDelegate(this, ::tierPriceCustomerGroup) as Column<String>
        val tierPriceQtyColumn = getDelegate(this, ::tierPriceQty) as Column<Double>
        val tierPriceColumn = getDelegate(this, ::tierPrice) as Column<Double>
        val tierPriceValueTypeColumn = getDelegate(this, ::tierPriceValueType) as Column<String>

        sku = data[skuColumn.name] as String
        tierPriceWebsite = data[tierPriceWebsiteColumn.name] as String
        tierPriceCustomerGroup = data[tierPriceCustomerGroupColumn.name] as String
        tierPriceQty = data[tierPriceQtyColumn.name] as Double
        tierPrice = data[tierPriceColumn.name] as Double
        tierPriceValueType = data[tierPriceValueTypeColumn.name] as String
    }
}