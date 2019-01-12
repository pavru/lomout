package net.pototskiy.apps.magemediation.api.database.mage

import net.pototskiy.apps.magemediation.api.database.getDelegate
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

abstract class StockSourceEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var sourceCode: String
    abstract var sku: String
    abstract var status: Boolean
    abstract var quantity: Double

    @Suppress("UNCHECKED_CAST")
    final override fun isNotEqual(data: Map<String, Any?>): Boolean {
        val statusColumn = getDelegate(this, ::status) as Column<Boolean>
        val quantityColumn = getDelegate(this, ::quantity) as Column<String>

        return status != data[statusColumn.name] as Boolean
                || quantity != data[quantityColumn.name] as Double
    }

    @Suppress("UNCHECKED_CAST")
    final override fun updateEntity(data: Map<String, Any?>) {
        val statusColumn = getDelegate(this, ::status) as Column<Boolean>
        val quantityColumn = getDelegate(this, ::quantity) as Column<String>

        transaction {
            status = data[statusColumn.name] as Boolean
            quantity = data[quantityColumn.name] as Double
        }
        wasUpdated()
    }

    @Suppress("UNCHECKED_CAST")
    final override fun setEntityData(data: Map<String, Any?>) {
        val sourceCodeColumn = getDelegate(this, ::sourceCode) as Column<String>
        val skuColumn = getDelegate(this, ::sku) as Column<String>
        val statusColumn = getDelegate(this, ::status) as Column<Boolean>
        val quantityColumn = getDelegate(this, ::quantity) as Column<String>

        sourceCode = data[sourceCodeColumn.name] as String
        sku = data[skuColumn.name] as String
        status = data[statusColumn.name] as Boolean
        quantity = data[quantityColumn.name] as Double
    }
}