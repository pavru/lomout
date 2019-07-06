package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

/**
 * Entity repository interface
 *
 */
interface EntityRepositoryInterface : AutoCloseable {

    /**
     * Create new entity
     *
     * @return New entity
     */
    fun create(type: KClass<out Document>): Document

    /**
     * Update entity
     *
     * @param entity Entity
     */
    fun update(entity: Document)

    /**
     * Update one attribute of the entity.
     *
     * @param entity The entity
     * @param attribute The attribute
     */
    fun updateAttribute(entity: Document, attribute: Attribute)

    /**
     * Update common properties (removed, absentDays, xxxxxxTime) of the entity.
     *
     * @param entity The entity
     */
    fun updateCommonPart(entity: Document)

    /**
     * Delete entity
     *
     * @param entity The entity to delete
     */
    fun delete(entity: Document)

    /**
     * Delete entity by the id
     *
     * @param id The entity id
     */
    fun delete(type: KClass<out Document>, id: ObjectId)
    // Getters
    /**
     * Get entity by the id
     *
     * @param id The entity id
     * @param includeDeleted The flag to include deleted entities
     * @return Entity? The entity, null if it does not exist
     */
    fun get(
        type: KClass<out Document>,
        id: ObjectId,
        includeDeleted: Boolean = true
    ): Document?

    /**
     * Get all entities by type
     *
     * @param type The entity type
     * @param includeDeleted The flag to include deleted entities
     * @return List<Entity> Found entities
     */
    fun get(
        type: KClass<out Document>,
        includeDeleted: Boolean = false
    ): List<Document>

    /**
     * Get entity by type and attribute values
     *
     * @param type The entity type
     * @param data Data to find entity
     * @return List<Entity> Found entities
     */
    fun get(
        type: KClass<out Document>,
        data: Map<Attribute, Any>,
        includeDeleted: Boolean = false
    ): Document?

    /**
     * Get entity by type and Bson filter
     *
     * @param type The entity type
     * @param filter The entity filter
     * @param includeDeleted The flag to include deleted
     */
    fun get(
        type: KClass<out Document>,
        vararg filter: Bson,
        includeDeleted: Boolean = false
    ): Document?

    /**
     * Get partial entity
     *
     * @param type The entity class
     * @param filter The filter
     * @param attributes Attributes to get
     * @param includeDeleted Flag to get deleted entity
     */
    fun get(
        type: KClass<out Document>,
        attributes: List<KMutableProperty1<out Document, *>>,
        vararg filter: Bson,
        includeDeleted: Boolean = false
    ): Document?

    // Bulk operations

    /**
     * Get all ids of the entity type
     *
     * @param type The entity type
     * @return List<Int> The list of ids
     */
    fun getIDs(
        type: KClass<out Document>,
        includeDeleted: Boolean = false
    ): List<ObjectId>

    /**
     * Get all ids of the entity type, paged
     *
     * @param type The entity type
     * @return List<Int> The list of ids
     */
    fun getIDs(
        type: KClass<out Document>,
        pageSize: Int,
        pageNumber: Int,
        includeDeleted: Boolean = false
    ): List<ObjectId>

    /**
     * Mark untouched entities as **removed**
     * @param type EntityType
     */
    fun markEntitiesAsRemoved(type: KClass<out Document>)

    /**
     * Update entities absent days.
     *
     * @param type The entity type
     */
    fun updateAbsentDays(type: KClass<out Document>)

    /**
     * Remove old entities.
     *
     * @param type The entity type
     * @param maxAbsentDays The maximum absent days
     */
    fun removeOldEntities(type: KClass<out Document>, maxAbsentDays: Int)

}
