package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.EType
import net.pototskiy.apps.magemediation.api.entity.Type

class EntityUpdater(private val eType: EType) {

    fun update(data: Map<AnyTypeAttribute, Type?>) {
        var entity = DbEntity.getEntitiesByAttributes(eType, data, true).firstOrNull()
        val filteredData = data.filterNot { it.key.isSynthetic || it.key.valueType is AttributeListType }
        entity?.wasUnchanged()
        if (entity == null) {
            @Suppress("UNCHECKED_CAST")
            entity = DbEntity.insertEntity(
                eType,
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
