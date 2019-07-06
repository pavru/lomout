package net.pototskiy.apps.lomout.api.entity

import kotlinx.coroutines.ObsoleteCoroutinesApi
import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.config.DatabaseConfig
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.document.Documents
import net.pototskiy.apps.lomout.api.toFilter
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.and
import org.litote.kmongo.deleteMany
import org.litote.kmongo.eq
import org.litote.kmongo.gt
import org.litote.kmongo.ne
import org.litote.kmongo.projection
import org.litote.kmongo.setTo
import org.litote.kmongo.updateMany
import org.litote.kmongo.updateOne
import java.time.Period
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance

/**
 * Entity repository implementation
 *
 * @constructor
 */
@UseExperimental(ObsoleteCoroutinesApi::class)
@Suppress("TooManyFunctions")
class EntityRepository(
    config: DatabaseConfig,
    sqlLogLevel: Level
) : EntityRepositoryInterface {
    @Suppress("MagicNumber")

    private val documents: Documents

    init {
        Configurator.setLevel(EXPOSED_LOG_NAME, sqlLogLevel)
        documents = Documents(config.name, config.server.host, config.server.port)
    }

    /**
     * Close repository, and it's caches
     *
     */
    override fun close() {
        documents.close()
    }

    /**
     * Create new entity
     *
     * @param type The entity type
     * @return The entity
     */
    override fun create(type: KClass<out Document>): Document {
        return try {
            type.createInstance()
        } catch (e: IllegalArgumentException) {
            throw AppConfigException(badPlace(type), "Entity type must have only one constructor without parameters.")
        }
    }

    /**
     * Update entity in the DB
     *
     * @param entity The entity
     */
    override fun update(entity: Document) {
        documents.update(entity)
    }

    /**
     * Update one attribute of the entity.
     *
     * @param entity The entity
     * @param attribute The attribute name
     */
    override fun updateAttribute(entity: Document, attribute: Attribute) {
        documents.getCollection(entity::class)
            .updateOne(
                Document::_id eq entity._id,
                attribute.property setTo entity.getAttribute(attribute)
            )
    }

    /**
     * Update common properties (removed, absentDays, xxxxxxTime) of the entity.
     *
     * @param entity The entity
     */
    override fun updateCommonPart(entity: Document) {
        documents.getCollection((entity::class))
            .updateOne(
                Document::_id eq entity._id,
                Document::createTime setTo entity.createTime,
                Document::updateTime setTo entity.updateTime,
                Document::removeTime setTo entity.removeTime,
                Document::toucheTime setTo entity.toucheTime,
                Document::removed setTo entity.removed,
                Document::absentDays setTo entity.absentDays
            )
    }

    /**
     * Delete entity from the DB
     *
     * @param entity The entity
     */
    override fun delete(entity: Document) {
        documents.deleteOne(entity)
    }

    /**
     * Delete entity from the DB
     *
     * @param id The entity ID
     */
    override fun delete(type: KClass<out Document>, id: ObjectId) {
        documents.deleteOne(type, id)
    }

    /**
     * Get entity from the DB by ID and it's status
     *
     * @param id The entity id
     * @param includeDeleted Flag to include deleted entities
     * @return The entity or null
     */
    override fun get(type: KClass<out Document>, id: ObjectId, includeDeleted: Boolean): Document? {
        return if (includeDeleted) {
            documents.getOne(type, id)
        } else {
            documents.getOne(
                type,
                Document::_id eq id,
                Document::removed ne true
            )
        }
    }

    /**
     * Get all entities from the DB by type and statuses
     *
     * @param type The entity type
     * @param includeDeleted Flag to include deleted entities
     * @return Entities list
     */
    override fun get(type: KClass<out Document>, includeDeleted: Boolean): List<Document> {
        return if (includeDeleted) {
            documents.getMany(type)
        } else {
            documents.getMany(type, Document::removed ne true)
        }
    }

    /**
     * Get entity by attribute values.
     *
     * @param type The entity type
     * @param data Attribute values
     * @param includeDeleted The flag to include deleted entities
     * @return The first entity that is fit to attribute values.
     */
    override fun get(type: KClass<out Document>, data: Map<Attribute, Any>, includeDeleted: Boolean): Document? {
        return if (includeDeleted) {
            documents.getOne(type, data.toFilter())
        } else {
            documents.getOne(type, and(Document::removed ne true, data.toFilter()))
        }
    }

    /**
     * Get entity by type and Bson filter
     *
     * @param type The entity type
     * @param filter The entity filter
     * @param includeDeleted The flag to include deleted
     */
    @Suppress("SpreadOperator")
    override fun get(type: KClass<out Document>, vararg filter: Bson, includeDeleted: Boolean): Document? {
        return if (includeDeleted) {
            documents.getOne(type, *filter)
        } else {
            documents.getOne(type, Document::removed ne true, *filter)
        }
    }

    /**
     * Get partial entity
     *
     * @param type The entity class
     * @param filter The filter
     * @param attributes Attributes to get
     * @param includeDeleted Flag to get deleted entity
     */
    @Suppress("SpreadOperator")
    override fun get(
        type: KClass<out Document>,
        attributes: List<KMutableProperty1<out Document, *>>,
        vararg filter: Bson,
        includeDeleted: Boolean
    ): Document? {
        val actualFilter = if (includeDeleted) and(Document::removed ne true, *filter) else and(*filter)
        return documents.getCollection(type)
            .find(actualFilter)
            .projection(*attributes.toTypedArray())
            .firstOrNull()
    }

    /**
     * Get all entity ids by the entity type.
     *
     * @param type The entity type
     * @param includeDeleted The flag to include delete entities
     * @return Entities list
     */
    override fun getIDs(type: KClass<out Document>, includeDeleted: Boolean): List<ObjectId> {
        return if (includeDeleted) {
            documents.getManyID(type)
        } else {
            documents.getManyID(type, Document::removed eq false)
        }
    }

    /**
     * Get all entity ids by the entity type.
     *
     * This is the paged request.
     *
     * @param type The entity type
     * @param pageSize The page size
     * @param pageNumber The page number
     * @param includeDeleted Tht flag to include deleted entities
     * @return Entities list
     */
    override fun getIDs(
        type: KClass<out Document>,
        pageSize: Int,
        pageNumber: Int,
        includeDeleted: Boolean
    ): List<ObjectId> {
        return if (includeDeleted) {
            documents.getManyID(type, pageSize, pageNumber)
        } else {
            documents.getManyID(type, pageSize, pageNumber, Document::removed eq false)
        }
    }

    /**
     * Mark untouched entities as REMOVED.
     *
     * @param type The entity type
     */
    override fun markEntitiesAsRemoved(type: KClass<out Document>) {
        val collection = documents.getCollection(type)
        collection
            .updateMany(
                and(
                    Document::toucheTime ne Documents.timestamp,
                    Document::removed ne true
                ),
                Document::removed setTo true,
                Document::removeTime setTo Documents.timestamp
            )
    }

    /**
     * Update entities absent days.
     *
     * @param type The entity type
     */
    override fun updateAbsentDays(type: KClass<out Document>) {
        val collection = documents.getCollection(type)
        collection
            .find(Document::removed eq true)
            .projection(Document::_id, Document::removeTime)
            .forEach {
                collection.updateOne(
                    Document::_id eq it._id,
                    Document::absentDays.setTo(
                        Period.between(it.removeTime!!.toLocalDate(), Documents.timestamp.toLocalDate()).days
                    )
                )
            }
    }

    /**
     * Remove old entities.
     *
     * @param type The entity type
     * @param maxAbsentDays The maximum days to absent
     */
    override fun removeOldEntities(type: KClass<out Document>, maxAbsentDays: Int) {
        documents.getCollection(type).deleteMany(
            Document::removed eq true,
            Document::absentDays gt maxAbsentDays
        )
    }
}
