package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.Type

/**
 * Pipeline entity data
 *
 * @property typeManager EntityTypeManager The entity type manager
 * @property entity DbEntity The related DB entity
 * @property inputEntity InputEntity The pipeline input entity definition
 * @property data MutableMap<Attribute<out Type>, Type?> The entity data
 * @property extData Map<Attribute<out Type>, Type?> The entity extend data
 * @constructor
 */
class PipelineData(
    private val typeManager: EntityTypeManager,
    val entity: DbEntity,
    private val inputEntity: InputEntity
) {
    /**
     * Entity data
     */
    val data = entity.data
    /**
     * Entity extend data
     */
    val extData by lazy { inputEntity.extendedAttributes(entity) }

    /**
     * Get entity attribute value
     *
     * @param attribute String The attribute name
     * @return Type?
     */
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

    /**
     * Get entity attribute by name
     *
     * @param name String The entity attribute name
     * @return Attribute<*>?
     */
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
