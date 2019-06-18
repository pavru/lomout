package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.database.EntityStatus
import net.pototskiy.apps.lomout.api.database.EntityStatus.*
import net.pototskiy.apps.lomout.api.entity.type.Type
import org.jetbrains.exposed.dao.EntityID

/**
 * Entity repository interface
 *
 */
interface EntityRepositoryInterface : AutoCloseable {

    /**
     * Entity type manager
     */
    val entityTypeManager: EntityTypeManagerImpl

    /**
     * Create new entity
     *
     * @return New entity
     */
    fun create(type: EntityType): Entity

    /**
     * Update entity
     *
     * @param entity Entity
     */
    fun update(entity: Entity)

    /**
     * Delete entity
     *
     * @param entity The entity to delete
     */
    fun delete(entity: Entity)

    /**
     * Delete entity by the id
     *
     * @param id The entity id
     */
    fun delete(id: EntityID<Int>)
    // Getters
    /**
     * Get entity by the id
     *
     * @param id The entity id
     * @param status Entity statuses to get
     * @return Entity? The entity, null if it does not exist
     */
    fun get(
        id: EntityID<Int>,
        vararg status: EntityStatus = arrayOf(UNCHANGED, CREATED, UPDATED, REMOVED)
    ): Entity?

    /**
     * Preload entity to cache
     *
     * @param id The entity id
     * @param status The entity status
     */
    suspend fun preload(
        id: EntityID<Int>,
        vararg status: EntityStatus = arrayOf(UNCHANGED, CREATED, UPDATED, REMOVED)
    )

    /**
     * Get all entities by type
     *
     * @param type The entity type
     * @param status Entity statues to get
     * @return List<Entity> Found entities
     */
    fun get(
        type: EntityType,
        vararg status: EntityStatus = arrayOf(UNCHANGED, CREATED, UPDATED, REMOVED)
    ): List<Entity>

    /**
     * Get entity by type and attribute values
     *
     * @param type The entity type
     * @param data Data to find entity
     * @param status Entity statuses to get
     * @return List<Entity> Found entities
     */
    fun get(
        type: EntityType,
        data: Map<AnyTypeAttribute, Type>,
        vararg status: EntityStatus = arrayOf(UNCHANGED, CREATED, UPDATED, REMOVED)
    ): Entity?

    /**
     * Preload entity to cache
     *
     * @param type The entity cache
     * @param data Entity attributes data to select entity
     * @param status The entity status
     */
    suspend fun preload(
        type: EntityType,
        data: Map<AnyTypeAttribute, Type>,
        vararg status: EntityStatus = arrayOf(UNCHANGED, CREATED, UPDATED, REMOVED)
    )

    // Attribute operations

    /**
     * Remove entity attribute
     *
     * @param entity The entity
     * @param attribute The entity attribute
     */
    fun deleteAttribute(entity: Entity, attribute: AnyTypeAttribute)

    /**
     * Update entity attribute
     *
     * @param entity The entity
     * @param attribute The entity attribute
     */
    fun updateAttribute(entity: Entity, attribute: AnyTypeAttribute)

    /**
     * Add an attribute to entity
     *
     * @param entity The entity
     * @param attribute The entity attribute
     */
    fun createAttribute(entity: Entity, attribute: AnyTypeAttribute)

    // Bulk operations

    /**
     * Get all ids of the entity type
     *
     * @param type The entity type
     * @return List<Int> The list of ids
     */
    fun getIDs(
        type: EntityType,
        vararg status: EntityStatus = arrayOf(CREATED, UPDATED, UNCHANGED, REMOVED)
    ): List<EntityID<Int>>

    /**
     * Get all ids of the entity type, paged
     *
     * @param type The entity type
     * @return List<Int> The list of ids
     */
    fun getIDs(
        type: EntityType,
        pageSize: Int,
        pageNumber: Int,
        vararg status: EntityStatus = arrayOf(CREATED, UPDATED, UNCHANGED, REMOVED)
    ): List<EntityID<Int>>

    fun resetTouchFlag(type: EntityType)
    fun markEntitiesAsRemoved(type: EntityType)
    fun updateAbsentDays(type: EntityType)
    fun removeOldEntities(type: EntityType, maxAbsentDays: Int)
}
