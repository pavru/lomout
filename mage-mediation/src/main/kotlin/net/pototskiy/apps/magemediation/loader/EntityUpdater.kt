package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.Type

class EntityUpdater(private val entityType: EntityType) {

    fun update(data: Map<AnyTypeAttribute, Type?>) {
        var entity = DbEntity.getEntitiesByAttributes(entityType, data, true).firstOrNull()
        val filteredData = data.filterNot { it.key.isSynthetic || it.key.valueType is AttributeListType }
        entity?.wasUnchanged()
        if (entity == null) {
            @Suppress("UNCHECKED_CAST")
            entity = DbEntity.insertEntity(
                entityType,
                filteredData.filterNot { it.value == null } as Map<AnyTypeAttribute, Type>
            )
            entity.wasCreated()
        } else {
            testAndUpdateTypedAttributes(entity, filteredData)
        }
    }

    private fun testAndUpdateTypedAttributes(entity: DbEntity, data: Map<AnyTypeAttribute, Type?>) {
        val storeData = DbEntity.readAttributes(entity)
        data.keys.plus(storeData.keys.minus(data.keys)).filter { !it.key }.forEach { attr ->
            val value = data[attr]
            val storedValue = storeData[attr]
            if (value != null && !value.isTransient && storedValue == null) {
                entity.addAttribute(attr, data.getValue(attr)!!)
                entity.wasUpdated(true)
            } else if (value != null && !value.isTransient && storedValue != null && value != storedValue) {
                entity.updateAttribute(attr, data.getValue(attr)!!)
                entity.wasUpdated(true)
            } else if (value == null && storedValue != null) {
                entity.removeAttribute(attr)
                entity.wasUpdated(true)
            }
        }
    }
}
