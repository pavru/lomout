package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.AttributeListType
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.Type

class EntityUpdater(private val entityType: EntityType) {

    fun update(data: Map<AnyTypeAttribute, Type?>): Long {
        val processedRows: Long
        var entity = DbEntity.getEntitiesByAttributes(entityType, data, true).firstOrNull()
        val filteredData = data.filterNot { it.key.isSynthetic || it.key.valueType == AttributeListType::class }
        entity?.wasUnchanged()
        if (entity == null) {
            @Suppress("UNCHECKED_CAST")
            entity = DbEntity.insertEntity(
                entityType,
                filteredData.filterNot { it.value == null } as Map<AnyTypeAttribute, Type>
            )
            entity.wasCreated()
            processedRows = 1L
        } else {
            processedRows = testAndUpdateTypedAttributes(entity, filteredData)
        }
        return processedRows
    }

    private fun testAndUpdateTypedAttributes(entity: DbEntity, data: Map<AnyTypeAttribute, Type?>): Long {
        var updatedRows = 0L
        val storeData = DbEntity.readAttributes(entity)
        data.keys.plus(storeData.keys.minus(data.keys))
            .filter { !it.key && it.builder == null }.forEach { attr ->
                val value = data[attr]
                val storedValue = storeData[attr]
                if (value != null && !value.isTransient && storedValue == null) {
                    entity.addAttribute(attr, data.getValue(attr)!!)
                    entity.wasUpdated(true)
                    updatedRows = 1L
                } else if (needToUpdate(value, storedValue)) {
                    entity.updateAttribute(attr, data.getValue(attr)!!)
                    entity.wasUpdated(true)
                    updatedRows = 1L
                } else if (value == null && storedValue != null) {
                    entity.removeAttribute(attr)
                    entity.wasUpdated(true)
                    updatedRows = 1L
                }
            }
        return updatedRows
    }

    private fun needToUpdate(
        value: Type?,
        storedValue: Type?
    ) = value != null && !value.isTransient && storedValue != null && value != storedValue
}
