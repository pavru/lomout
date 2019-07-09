package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.unknownPlace
import kotlin.reflect.KClass

/**
 * Entity collection, contains only one entity per type
 *
 * @property data Entities
 * @constructor
 */
class EntityCollection(private val data: List<Document>) : List<Document> by data {
    private val entityMap = data.map { it::class to it }.toMap()
    /**
     * Get entity by type name
     *
     * @param type The entity type name
     * @return Entity
     * @throws AppDataException Entity not found
     */
    operator fun get(type: KClass<out Document>): Document {
        return entityMap[type]
            ?: throw AppDataException(
                unknownPlace(),
                message("message.error.pipeline.no_entity_in_collection", type.qualifiedName)
            )
    }

    /**
     * Get entity by type name
     *
     * @param type The entity type name
     * @return The entity or null
     */
    fun getOrNull(type: KClass<out Document>): Document? = entityMap[type]
}
