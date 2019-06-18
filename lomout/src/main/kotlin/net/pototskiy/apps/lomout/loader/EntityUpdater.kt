package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.database.EntityStatus.*
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.Entity
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.type.ATTRIBUTELIST
import net.pototskiy.apps.lomout.api.entity.type.Type

class EntityUpdater(
    private val repository: EntityRepositoryInterface,
    private val entityType: EntityType
) {

    fun update(data: Map<AnyTypeAttribute, Type>): Long {
        val processedRows: Long
        var entity = repository.get(
            entityType,
            data.filterKeys { it.key },
            CREATED, UPDATED, UNCHANGED, REMOVED
        )
        val filteredData = data.filterNot { it.key.isSynthetic || it.key.type == ATTRIBUTELIST::class }
        entity?.wasUnchanged()
        if (entity == null) {
            entity = repository.create(entityType)
            filteredData.forEach { entity[it.key] = it.value }
            entity.wasCreated()
            processedRows = 1L
        } else {
            processedRows = testAndUpdateTypedAttributes(entity, filteredData)
        }
        repository.update(entity)
        return processedRows
    }

    private fun testAndUpdateTypedAttributes(entity: Entity, data: Map<AnyTypeAttribute, Type>): Long {
        var updatedRows = 0L
        val storeData = entity.data
        data.keys.union(storeData.keys)
            .filter { !it.key && !it.isSynthetic }.forEach { attr ->
                val value = data[attr]
                val storedValue = storeData[attr]
                if (value != null && storedValue == null || needToUpdate(value, storedValue)) {
                    entity[attr] = data.getValue(attr)
                    entity.wasUpdated(true)
                    updatedRows = 1L
                } else if (value == null && storedValue != null) {
                    entity[attr] = null
                    entity.wasUpdated(true)
                    updatedRows = 1L
                }
            }
        return updatedRows
    }

    private fun needToUpdate(
        value: Type?,
        storedValue: Type?
    ) = value != null && storedValue != null && value != storedValue
}
