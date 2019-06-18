package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.unknownPlace

/**
 * Entity collection, contains only one entity per type
 *
 * @property data Entities
 * @constructor
 */
class EntityCollection(private val data: List<Entity>) : List<Entity> by data {
    /**
     * Get entity by type name
     *
     * @param typeName The entity type name
     * @return Entity
     * @throws AppDataException Entity not found
     */
    operator fun get(typeName: String): Entity {
        return data.firstOrNull { it.type.name == typeName }
            ?: throw AppDataException(unknownPlace(), "There is no entity with type $typeName in pipeline data")
    }

    /**
     * Get entity by entity type
     * @param type The entity type
     * @return Entity
     * @throws AppDataException Entity not found
     */
    operator fun get(type: EntityType): Entity {
        return data.firstOrNull { it.type == type }
            ?: throw AppDataException(unknownPlace(), "There is no entity with type ${type.name} in pipeline data")
    }

    /**
     * Get entity by type name
     *
     * @param typeName The entity type name
     * @return Entity?
     */
    fun getOrNull(typeName: String): Entity? = data.firstOrNull { it.type.name == typeName }

    /**
     * Get entity by entity type
     *
     * @param type The entity type
     *
     * @return Entity?
     */
    @Suppress("unused")
    fun getOrNull(type: EntityType): Entity? = data.firstOrNull { it.type == type }
}
