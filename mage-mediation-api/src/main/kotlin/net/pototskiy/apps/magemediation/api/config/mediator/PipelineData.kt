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
        val attr = EntityAttributeManager.getAttributeOrNull(AttributeName(entity.eType.name, attribute))
        return when {
            attr != null -> data[attr]
            inputEntity.entityExtension != null ->
                EntityAttributeManager.getAttributeOrNull(
                    AttributeName(inputEntity.entityExtension.name, attribute)
                )?.let { extData[it] }
            else -> null
        }
    }

    fun findAttribute(name: String): Attribute<*>? {
        val attr = EntityAttributeManager.getAttributeOrNull(AttributeName(entity.eType.name, name))
        return when {
            attr != null -> attr
            inputEntity.entityExtension != null -> EntityAttributeManager.getAttributeOrNull(
                AttributeName(inputEntity.entityExtension.name, name)
            )
            else -> null
        }
    }
}
