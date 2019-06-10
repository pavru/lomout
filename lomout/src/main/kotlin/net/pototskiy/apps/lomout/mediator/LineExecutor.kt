package net.pototskiy.apps.lomout.mediator

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.AppException
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.config.mediator.PipelineData
import net.pototskiy.apps.lomout.api.config.pipeline.ClassifierElement
import net.pototskiy.apps.lomout.api.config.pipeline.PipelineDataCache
import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.database.EntityIdCol
import net.pototskiy.apps.lomout.api.database.EntityTab
import net.pototskiy.apps.lomout.api.database.EntityTypeCol
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.unknownPlace
import org.apache.commons.collections4.map.LRUMap
import org.apache.logging.log4j.Logger
import org.cache2k.Cache
import org.cache2k.Cache2kBuilder
import org.cache2k.CacheManager
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

abstract class LineExecutor(
    val entityTypeManager: EntityTypeManager,
    cacheSizeProperty: Int
) : PipelineDataCache {
    private val maxCacheSize = if (cacheSizeProperty == 0) MAX_CACHE_SIZE else cacheSizeProperty
    private val initialCacheSize = if (cacheSizeProperty == 0) INITIAL_CACHE_SIZE else cacheSizeProperty / 2
    private lateinit var line: AbstractLine

    protected val pipelineDataCache = LRUMap<Int, PipelineData>(maxCacheSize, initialCacheSize)

    protected val dataCache: Cache<Int, PipelineData> by lazy {
        CacheManager.getInstance().getCache<Int, PipelineData>("pipelineData")
            ?: object : Cache2kBuilder<Int, PipelineData>() {}
                .name("pipelineData")
                .enableJmx(true)
                .entryCapacity(MAX_CACHE_SIZE.toLong())
                .eternal(true)
                .build()
    }

    protected abstract val logger: Logger
    private val jobs = mutableListOf<Job>()
    protected var processedRows = 0L

    abstract fun processResultData(data: Map<AnyTypeAttribute, Type?>): Long
    abstract fun preparePipelineExecutor(line: AbstractLine): PipelineExecutor

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    open fun executeLine(line: AbstractLine): Long {
        this.line = line
        processedRows = 0L
        try {
            runBlocking {
                val pipeline = preparePipelineExecutor(line)
                val inputChannel: Channel<ClassifierElement> = Channel()
                jobs.add(launch {
                    pipeline.execute(inputChannel).consumeEach {
                        try {
                            processedRows += processResultData(it)
                        } catch (e: AppException) {
                            logger.error("Cannot process entity, {}", e.message)
                            logger.trace("Cause: ", e)
                        }
                    }
                })
                topLevelInput(line).forEach { inputChannel.send(it) }
                inputChannel.close()
                joinAll(*jobs.toTypedArray())
            }
        } catch (e: Exception) {
            processException(e)
        }
        return processedRows
    }

    override fun readEntity(id: EntityID<Int>): PipelineData {
        var pipelineData = dataCache.get(id.value)
        if (pipelineData == null) {
            val entity = transaction { DbEntity.findById(id) }
                ?: throw AppDataException(unknownPlace(), "Matched entity id '${id.value}' cannot be found.")
            entity.readAttributes()
            val inputEntity = line.inputEntities.find { it.entity.name == entity.eType.name }
                ?: throw AppDataException(badPlace(entity.eType), "Unexpected input entity.")
            pipelineData = PipelineData(entityTypeManager, entity, inputEntity)
            dataCache.put(entity.id.value, pipelineData)
        }
        return pipelineData
    }

    private suspend fun topLevelInput(line: AbstractLine) =
        sequence<ClassifierElement> {
            line.inputEntities.forEach { input ->
                var offset = 0
                do {
                    val items = transaction {
                        val alias = EntityTab.alias("inputEntity")
                        var where = Op.build { alias[EntityTypeCol] eq input.entity }
                        input.filter?.let { where = where.and(it.where(alias)) }
                        alias
                            .slice(alias[EntityIdCol])
                            .select { where }
                            .limit(PAGE_SIZE, offset)
                            .toList()
                            .map { it[alias[EntityIdCol]] }
                    }
                    items.forEach {
                        yield(
                            ClassifierElement.Mismatched(
                                listOf(ClassifierElement.ElementID(input.entity, it)),
                                this@LineExecutor
                            )
                        )
                    }
                    offset += PAGE_SIZE
                } while (items.isNotEmpty())
            }
        }

    private fun processException(e: Exception) {
        logger.error("{}", e.message)
        logger.trace("Caused by:", e)
    }

    companion object {
        const val PAGE_SIZE = 1000
        const val MAX_CACHE_SIZE = 1000
        const val INITIAL_CACHE_SIZE = 300
    }
}
