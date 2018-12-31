package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.getDelegate
import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

abstract class CustomerGroupEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var customerGroupID: Long
    abstract var customerGroupCode: String
    abstract var taxClassID: Long

    @Suppress("UNCHECKED_CAST")
    final override fun isNotEqual(data: Map<String, Any?>): Boolean {
        val customerGroupCodeColumn = getDelegate(this, ::customerGroupCode) as Column<Long>
        val taxClassIDColumn = getDelegate(this, ::taxClassID) as Column<Long>

        return customerGroupCode != data[customerGroupCodeColumn.name] as String
                || taxClassID != data[taxClassIDColumn.name] as Long
    }

    @Suppress("UNCHECKED_CAST")
    final override fun updateEntity(data: Map<String, Any?>) {
        val customerGroupCodeColumn = getDelegate(this, ::customerGroupCode) as Column<Long>
        val taxClassIDColumn = getDelegate(this, ::taxClassID) as Column<Long>

        transaction {
            customerGroupCode = data[customerGroupCodeColumn.name] as String
            taxClassID = data[taxClassIDColumn.name] as Long
        }
        wasUpdated()
    }

    @Suppress("UNCHECKED_CAST")
    final override fun setEntityData(data: Map<String, Any?>) {
        val customerGroupIDColumn = getDelegate(this, ::customerGroupID) as Column<Long>
        val customerGroupCodeColumn = getDelegate(this, ::customerGroupCode) as Column<Long>
        val taxClassIDColumn = getDelegate(this, ::taxClassID) as Column<Long>

        customerGroupID = data[customerGroupIDColumn.name] as Long
        customerGroupCode = data[customerGroupCodeColumn.name] as String
        taxClassID = data[taxClassIDColumn.name] as Long
    }
}