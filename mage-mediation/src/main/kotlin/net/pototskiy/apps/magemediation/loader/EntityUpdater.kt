package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.database.EntityClass
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntityClass

class EntityUpdater(private val entityClass: EntityClass<PersistentSourceEntity>) {

    fun update(data: Map<Attribute, Any>) {
        var entity = entityClass.getEntityByKeys(data)
        entity?.wasUnchanged()
        if (entity == null) {
            entity = (entityClass.backend as PersistentSourceEntityClass).insertNewRecord(entityClass, data)
            entity.wasCreated()
        } else {
            testAndUpdateTypedAttributes(entity, data)
        }
    }

    private fun testAndUpdateTypedAttributes(entity: PersistentSourceEntity, data: Map<Attribute, Any?>) {
        @Suppress("UNCHECKED_CAST")
        val storeData = entityClass.readAttributes(entity)
        data.keys.plus(storeData.keys.minus(data.keys)).filter { !it.key }.forEach { attr ->
            if (data[attr] != null && storeData[attr] == null) {
                entity.addAttribute(attr, data.getValue(attr)!!)
                entity.wasUpdated()
            } else if (data[attr] != null && storeData[attr] != null && data[attr] != storeData[attr]) {
                entity.updateAttribute(attr, data.getValue(attr)!!)
                entity.wasUpdated()
            } else if (data[attr] == null && storeData[attr] != null) {
                entity.removeAttribute(attr)
                entity.wasUpdated()
            }
        }
    }

}
