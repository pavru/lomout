package net.pototskiy.apps.lomout.api.entity

import com.mysql.cj.jdbc.MysqlDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.TIMESTAMP
import net.pototskiy.apps.lomout.api.config.DatabaseConfig
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.database.DbSchema
import net.pototskiy.apps.lomout.api.entity.helper.findEntityByAttributes
import net.pototskiy.apps.lomout.api.entity.type.ListType
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.entity.type.table
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.cache2k.Cache2kBuilder
import org.cache2k.integration.CacheLoader
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.Duration
import java.util.*
import kotlin.system.exitProcess

/**
 * Entity repository implementation
 *
 * @property entityTypeManager Entity type manager
 * @property entityCache The entity cache
 * @property dataIdCache The entity id cache
 * @constructor
 */
class EntityRepository(
    config: DatabaseConfig,
    override val entityTypeManager: EntityTypeManagerImpl,
    sqlLogLevel: Level
) : EntityRepositoryInterface {

    /**
     * Close repository, and it's caches
     *
     */
    override fun close() {
        entityCache.close()
        dataIdCache.close()
    }

    private val entityCache = object : Cache2kBuilder<EntityID<Int>, Entity?>() {}
        .name("entityRepositoryCache")
        .enableJmx(true)
        .entryCapacity(CACHE_SIZE)
        .permitNullValues(true)
        .eternal(true)
        .loaderThreadCount(LOADER_THREAD_COUNT)
        .loader(Loader(this))
        .storeByReference(true)
        .build()

    private val dataIdCache = object : Cache2kBuilder<DataKey, EntityID<Int>>() {}
        .name("dataEntityIdCache")
        .enableJmx(true)
        .entryCapacity(CACHE_SIZE)
        .eternal(true)
        .build()

    init {
        initDatabase(config, entityTypeManager, sqlLogLevel)
    }

    /**
     * Create new entity
     *
     * @param type The entity type
     * @return The entity
     */
    override fun create(type: EntityType): Entity {
        val id = transaction {
            DbEntityTable.insertAndGetId {
                it[entityType] = type
                it[touchedInLoading] = true
                it[previousStatus] = EntityStatus.CREATED
                it[currentStatus] = EntityStatus.CREATED
                it[created] = TIMESTAMP
                it[updated] = TIMESTAMP
                it[absentDays] = 0
            }
        }
        return Entity(type, id, this).apply {
            touchedInLoading = true
            previousStatus = EntityStatus.CREATED
            currentStatus = EntityStatus.CREATED
            created = TIMESTAMP
            updated = TIMESTAMP
            absentDays = 0
            entityCache.put(id, this)
        }
    }

    /**
     * Update entity in the DB
     *
     * @param entity The entity
     */
    override fun update(entity: Entity) {
        transaction {
            DbEntityTable.update({ DbEntityTable.id eq entity.id }) {
                it[touchedInLoading] = entity.touchedInLoading
                it[currentStatus] = entity.currentStatus
                it[previousStatus] = entity.previousStatus
                it[created] = entity.created
                it[updated] = entity.updated
                it[removed] = entity.removed
                it[absentDays] = entity.absentDays
            }
        }
        entityCache.put(entity.id, entity)
    }

    /**
     * Delete entity from the DB
     *
     * @param entity The entity
     */
    override fun delete(entity: Entity) = delete(entity.id)

    /**
     * Delete entity from the DB
     *
     * @param id The entity ID
     */
    override fun delete(id: EntityID<Int>) {
        entityCache.remove(id)
        transaction { DbEntityTable.deleteWhere { DbEntityTable.id eq id } }
    }

    /**
     * Get entity from the DB by ID and it's status
     *
     * @param id The entity id
     * @param status Entity statuses
     * @param startPrefetch Prefetch entities with next id
     * @return The entity or null
     */
    override fun get(id: EntityID<Int>, vararg status: EntityStatus, startPrefetch: Boolean): Entity? {
        val entity = entityCache[id] ?: return null
        return if (entity.currentStatus in status) {
            if (startPrefetch) startPrefetch(entity.type, id)
            entity
        } else {
            null
        }
    }

    /**
     * Preload entity to cache by the ID
     *
     * @param id The entity id
     */
    override suspend fun preload(id: EntityID<Int>) {
        if (!entityCache.containsKey(id)) {
            GlobalScope.launch(Dispatchers.IO) {
                transaction {
                    DbEntityTable.select { DbEntityTable.id eq id }.firstOrNull()
                }?.also {
                    val entity = it.toEntity(this@EntityRepository)
                    entity.loadAttributes()
                    entityCache.putIfAbsent(entity.id, entity)
                }
            }
        }
    }

    /**
     * Get all entities from the DB by type and statuses
     *
     * @param type The entity type
     * @param status Entity statuses
     * @return Entities list
     */
    override fun get(type: EntityType, vararg status: EntityStatus): List<Entity> {
        val entities = transaction {
            DbEntityTable.select { DbEntityTable.entityType eq type }
                .map {
                    it.toEntity(this@EntityRepository).apply {
                        loadAttributes()
                    }
                }
        }
        if (entities.size <= CACHE_SIZE_FOR_BULK) {
            entityCache.putAll(entities.map { it.id to it }.toMap())
        }
        return entities.filter { it.currentStatus in status }
    }

    /**
     * Get entity by attribute values.
     *
     * @param type The entity type
     * @param data Attribute values
     * @param status Entity statuses
     * @return The first entity that is fit to attribute values.
     */
    override fun get(type: EntityType, data: Map<AnyTypeAttribute, Type>, vararg status: EntityStatus): Entity? {
        val key = DataKey(type, data)
        val candidate = dataIdCache[key]
        val entity = candidate?.let { entityCache[candidate] }
        return if (entity != null && data.all { it.value == entity[it.key] }) {
            if (entity.currentStatus in status) {
                entityCache[entity.id]
            } else {
                null
            }
        } else {
            dataIdCache.remove(key)
            findEntityByAttributes(type, data, this)?.also {
                it.loadAttributes()
                entityCache.put(it.id, it)
                dataIdCache.put(DataKey(type, data), it.id)
            }?.also {
                startPrefetch(type, it.id)
            }
        }
    }

    /**
     * Preload entity to the cache by attribute values.
     *
     * Only one fitted entity is preloaded.
     *
     * @param type The entity type
     * @param data Attribute values
     */
    override suspend fun preload(type: EntityType, data: Map<AnyTypeAttribute, Type>) {
        if (!dataIdCache.containsKey(DataKey(type, data))) {
            GlobalScope.launch(Dispatchers.IO) {
                findEntityByAttributes(type, data, this@EntityRepository)?.also { entity ->
                    entity.loadAttributes()
                    if (entityCache.putIfAbsent(entity.id, entity)) {
                        dataIdCache.put(DataKey(type, data), entity.id)
                    }
                }
            }
        }
    }

    /**
     * Get all entity ids by the entity type.
     *
     * @param type The entity type
     * @param status Entity statuses
     * @return Entities list
     */
    override fun getIDs(type: EntityType, vararg status: EntityStatus): List<EntityID<Int>> {
        return transaction {
            DbEntityTable
                .slice(DbEntityTable.id)
                .select { (DbEntityTable.entityType eq type) and (DbEntityTable.currentStatus inList status.toList()) }
                .map { it[DbEntityTable.id] }
                .toList()
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
     * @param status Entity statuses
     * @return Entities list
     */
    override fun getIDs(
        type: EntityType,
        pageSize: Int,
        pageNumber: Int,
        vararg status: EntityStatus
    ): List<EntityID<Int>> {
        return transaction {
            DbEntityTable
                .slice(DbEntityTable.id)
                .select { (DbEntityTable.entityType eq type) and (DbEntityTable.currentStatus inList status.toList()) }
                .limit(pageSize, pageNumber * pageSize)
                .map { it[DbEntityTable.id] }
                .toList()
        }
    }

    /**
     * Reset touch flag of entities.
     *
     * @param type The entity type
     */
    override fun resetTouchFlag(type: EntityType) {
        transaction {
            DbEntityTable.update({ DbEntityTable.entityType eq type }) {
                it[touchedInLoading] = false
            }
        }
        entityCache.entries()
            .filter { it.value != null && it.value?.type == type }
            .forEach { it.value?.touchedInLoading = false }
    }

    /**
     * Mark untouched entities as REMOVED.
     *
     * @param type The entity type
     */
    override fun markEntitiesAsRemoved(type: EntityType) {
        transaction {
            DbEntityTable.update({
                (DbEntityTable.entityType eq type)
                    .and(DbEntityTable.touchedInLoading eq false)
                    .and(DbEntityTable.currentStatus neq EntityStatus.REMOVED)
            }) {
                it.update(previousStatus, currentStatus)
                it[currentStatus] = EntityStatus.REMOVED
                it[removed] = TIMESTAMP
            }
        }
        entityCache.entries()
            .forEach {
                val entity = it.value
                if (false == entity?.touchedInLoading && entity.type == type &&
                    entity.currentStatus != EntityStatus.REMOVED
                ) {
                    entity.previousStatus = entity.currentStatus
                    entity.currentStatus = EntityStatus.REMOVED
                    entity.removed = TIMESTAMP
                }
            }
    }

    /**
     * Update entities absent days.
     *
     * @param type The entity type
     */
    override fun updateAbsentDays(type: EntityType) {
        transaction {
            DbEntityTable
                .slice(DbEntityTable.id, DbEntityTable.removed)
                .select {
                    (DbEntityTable.entityType eq type)
                        .and(DbEntityTable.currentStatus eq EntityStatus.REMOVED)
                }.toList()
        }.forEach { row ->
            transaction {
                DbEntityTable.update({ DbEntityTable.id eq row[DbEntityTable.id] }) {
                    it[absentDays] = Duration(row[removed], TIMESTAMP).standardDays.toInt()
                }
            }
        }
        entityCache.entries().forEach {
            val entity = it.value
            if (EntityStatus.REMOVED == entity?.currentStatus && entity.type == type) {
                entity.absentDays = Duration(entity.removed, TIMESTAMP).standardDays.toInt()
            }
        }
    }

    /**
     * Remove old entities.
     *
     * @param type The entity type
     * @param maxAbsentDays The maximum days to absent
     */
    override fun removeOldEntities(type: EntityType, maxAbsentDays: Int) {
        transaction {
            DbEntityTable.deleteWhere {
                (DbEntityTable.entityType eq type)
                    .and(DbEntityTable.absentDays greaterEq maxAbsentDays)
                    .and(DbEntityTable.currentStatus eq EntityStatus.REMOVED)
            }
        }
        entityCache.entries().forEach {
            val entity = it.value
            if (EntityStatus.REMOVED == entity?.currentStatus && entity.type == type &&
                entity.absentDays >= maxAbsentDays
            ) {
                entityCache.remove(it.key)
            }
        }
    }

    /**
     *
     * Delete entity attribute in the DB.
     *
     * @param entity The entity
     * @param attribute The attribute to delete
     */
    @Synchronized
    override fun deleteAttribute(entity: Entity, attribute: AnyTypeAttribute) {
        internalDeleteAttribute(entity, attribute)
        entityCache.put(entity.id, entity)
    }

    /**
     * Delete entity attribute from the DB without cache update.
     *
     * @param entity The entity
     * @param attribute The attribute to delete
     */
    private fun internalDeleteAttribute(
        entity: Entity,
        attribute: AnyTypeAttribute
    ) {
        if (!attribute.isSynthetic) {
            val table = attribute.type.table
            transaction {
                table.deleteWhere { (table.owner eq entity.id) and (table.code eq attribute.name) }
            }
        }
    }

    /**
     * Update entity attribute in the DB.
     *
     * @param entity The entity
     * @param attribute The entity attribute
     */
    @Synchronized
    override fun updateAttribute(entity: Entity, attribute: AnyTypeAttribute) {
        if (!attribute.isSynthetic) {
            internalDeleteAttribute(entity, attribute)
            internalCreateAttribute(entity, attribute)
        }
        entityCache.put(entity.id, entity)
    }

    /**
     * Create entity attribute in the DB.
     *
     * @param entity The entity
     * @param attribute The entity attribute
     */
    @Synchronized
    override fun createAttribute(entity: Entity, attribute: AnyTypeAttribute) {
        internalCreateAttribute(entity, attribute)
        entityCache.put(entity.id, entity)
    }

    /**
     * Create entity attribute in the DB without cache update.
     *
     * @param entity The entity
     * @param attribute The entity attribute
     */
    private fun internalCreateAttribute(
        entity: Entity,
        attribute: AnyTypeAttribute
    ) {
        val value = entity[attribute]
        if (!attribute.isSynthetic && value != null) {
            val table = attribute.type.table
            if (value.isSingle()) {
                transaction {
                    table.insert {
                        it[table.owner] = entity.id
                        it[table.code] = attribute.name
                        it[table.index] = -1
                        it[table.value] = value
                    }
                }
            } else {
                value as ListType<*>
                transaction {
                    table.batchInsert(value.withIndex()) { data ->
                        this[table.owner] = entity.id
                        this[table.code] = attribute.name
                        this[table.index] = data.index
                        this[table.value] = data.value
                    }
                }
            }
        }
    }

    /**
     * Start prefetch entity by the ID.
     *
     * @param type The entity type
     * @param id The entity id
     */
    private fun startPrefetch(type: EntityType, id: EntityID<Int>) = runBlocking<Unit> {
        launch {
            val ids = transaction {
                DbEntityTable
                    .slice(DbEntityTable.id)
                    .select { (DbEntityTable.entityType eq type) and (DbEntityTable.id greater id) }
                    .limit(READ_AHEAD_COUNT)
                    .map { it[DbEntityTable.id] }
                    .toList()
            }
            entityCache.prefetchAll(ids.reversed(), null)
        }
    }

    /**
     * Entity id cache key.
     *
     * @property type The entity type
     * @property data Attribute values
     * @constructor
     */
    private data class DataKey(
        val type: EntityType,
        val data: Map<AnyTypeAttribute, Type>
    )

    /**
     * Cache entity loader.
     *
     * @property repository The entity repository
     * @constructor
     */
    private class Loader(private val repository: EntityRepositoryInterface) : CacheLoader<EntityID<Int>, Entity?>() {
        override fun load(key: EntityID<Int>?): Entity? {
            val row = key?.let { id ->
                transaction {
                    DbEntityTable.select { DbEntityTable.id eq id }.firstOrNull()
                }
            } ?: return null
            return row.toEntity(repository).also { it.loadAttributes() }
        }
    }

    /**
     * Check and create the database schema.
     *
     * @param config The database configuration
     * @param entityTypeManager The entity type manager
     * @param logLevel Log level
     */
    private fun initDatabase(
        config: DatabaseConfig,
        entityTypeManager: EntityTypeManager,
        logLevel: Level = Level.ERROR
    ) {
        val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
        Configurator.setLevel(EXPOSED_LOG_NAME, logLevel)
        statusLog.info("Database has stated to check and init")
        val datasource = MysqlDataSource()
        datasource.setURL("jdbc:mysql://${config.server.host}:${config.server.port}/${config.name}")
        datasource.user = config.server.user
        datasource.password = config.server.password
        datasource.serverTimezone = TimeZone.getDefault().id

        @Suppress("TooGenericExceptionCaught")
        try {
            val db = Database.connect(datasource)
            statusLog.info("DB dialect: ${db.dialect.name}")
            DbSchema.createSchema(entityTypeManager)
        } catch (e: Exception) {
            statusLog.error("Cannot init DB", e)
            exitProcess(1)
        }
        statusLog.info("Database has finished to check and init")
    }

    companion object {
        private const val CACHE_SIZE = 5500L
        private const val CACHE_SIZE_FOR_BULK = CACHE_SIZE / 4L
        private const val READ_AHEAD_COUNT = 30
        private const val LOADER_THREAD_COUNT = 5
    }
}
