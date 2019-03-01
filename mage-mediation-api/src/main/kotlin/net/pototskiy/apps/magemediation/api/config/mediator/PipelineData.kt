package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeName
import net.pototskiy.apps.magemediation.api.entity.EntityAttributeManager
import net.pototskiy.apps.magemediation.api.entity.Type

class PipelineData(
    val entity: DbEntity,
    private val inputEntity: InputEntity
) {
    val data = entity.data
    val extData by lazy { inputEntity.extendedAttributes(entity) }

    operator fun get(attribute: String): Type? {
        val attr = EntityAttributeManager.getAttributeOrNull(AttributeName(entity.eType.type, attribute))
        if (attr != null) {
            return data[attr]
        } else if (inputEntity.entityExtension != null) {
            val extAttr = EntityAttributeManager.getAttributeOrNull(
                AttributeName(inputEntity.entityExtension.type, attribute)
            ) ?: return null
            return extData[extAttr]
        }
        return null
    }

    fun findAttribute(name: String): Attribute<*>? {
        val attr = EntityAttributeManager.getAttributeOrNull(AttributeName(entity.eType.type, name))
        if (attr != null) {
            return attr
        } else if (inputEntity.entityExtension != null) {
            return EntityAttributeManager.getAttributeOrNull(
                AttributeName(inputEntity.entityExtension.type, name)
            ) ?: return null
        }
        return null
    }
}
