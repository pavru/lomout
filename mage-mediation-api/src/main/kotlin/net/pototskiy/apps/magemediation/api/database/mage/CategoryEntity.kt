package net.pototskiy.apps.magemediation.api.database.mage

import net.pototskiy.apps.magemediation.api.database.getDelegate
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column

abstract class CategoryEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var entityID: Long

    final override fun isNotEqual(data: Map<String, Any?>): Boolean = false

    final override fun updateEntity(data: Map<String, Any?>) {
        wasUpdated()
    }

    @Suppress("UNCHECKED_CAST")
    final override fun setEntityData(data: Map<String, Any?>) {
        val entityIDColumn = getDelegate(this, ::entityID) as Column<Long>
        entityID = data[entityIDColumn.name] as Long
    }
}