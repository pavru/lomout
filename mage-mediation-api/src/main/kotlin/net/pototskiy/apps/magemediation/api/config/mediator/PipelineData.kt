package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.Type

class PipelineData(
    private val typeManager: EntityTypeManager,
    val entity: DbEntity,
    private val inputEntity: InputEntity
) {
    val data = entity.data
    val extData by lazy { inputEntity.extendedAttributes(entity) }

    operator fun get(attribute: String): Type? {
        val attr = typeManager.getEntityAttribute(entity.eType, attribute)
        return when {
            attr != null -> data[attr]
            inputEntity.entityExtension != null ->
                typeManager.getEntityAttribute(inputEntity.entityExtension, attribute)
                    ?.let { extData[it] }
            else -> null
        }
    }

    fun findAttribute(name: String): Attribute<*>? {
        val attr = typeManager.getEntityAttribute(entity.eType, name)
        return when {
            attr != null -> attr
            inputEntity.entityExtension != null -> typeManager
                .getEntityAttribute(inputEntity.entityExtension, name)
            else -> null
        }
    }
}
