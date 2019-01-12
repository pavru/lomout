package net.pototskiy.apps.magemediation.api.database.mage

import net.pototskiy.apps.magemediation.api.database.getDelegate
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column

abstract class ProductEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var sku: String

    final override fun isNotEqual(data: Map<String, Any?>): Boolean = false

    final override fun updateEntity(data: Map<String, Any?>) {
        wasUpdated()
    }

    final override fun setEntityData(data: Map<String, Any?>) {
        val skuColumn = getDelegate(this, ::sku) as Column<*>
        sku = data[skuColumn.name] as String
    }
}