package net.pototskiy.apps.magemediation.mediator

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import net.pototskiy.apps.magemediation.api.MEDIATOR_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineData
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.magemediation.api.config.mediator.ProductionLine
import net.pototskiy.apps.magemediation.api.database.EntityClass
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.database.schema.SourceEntities
import net.pototskiy.apps.magemediation.api.database.schema.SourceEntity
import net.pototskiy.apps.magemediation.database.BooleanConst
import net.pototskiy.apps.magemediation.database.PipelineSets
import net.pototskiy.apps.magemediation.database.StringConst
import net.pototskiy.apps.magemediation.loader.EntityUpdater
import org.apache.commons.collections4.map.LRUMap
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class CrossProductionLineExecutor {

    private val entityCache = LRUMap<Int, PersistentSourceEntity>(1000, 300)
    private val logger = LogManager.getLogger(MEDIATOR_LOG_NAME)
    private val jobs = mutableListOf<Job>()

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    fun executeLine(line: ProductionLine) {
        try {
            runBlocking {
                @Suppress("UNCHECKED_CAST")
                val targetEntityClass = EntityClass.getOrRegisterClass(
                    EntityClass(
                        line.outputEntity.name,
                        SourceEntity,
                        line.outputEntity.attributes,
                        line.outputEntity.open
                    )
                ) as EntityClass<PersistentSourceEntity>
                val entityUpdater = EntityUpdater(targetEntityClass)
                val pipeline = PipelineExecutor(line.pipeline, line.inputEntities, line.outputEntity, entityCache)
                createTopPipelineSet(line)
                val (from, where, columns) = mainQuery(line)
                val inputChannel: Channel<PipelineDataCollection> = Channel()
                jobs.add(launch {
                    pipeline.execute(inputChannel).consumeEach {
                        entityUpdater.update(it)
                    }
                })
                rowSequence(from, where, columns) { row ->
                    createPipelineData(columns, row, line)
                }.forEach { inputChannel.send(PipelineDataCollection(it)) }
                inputChannel.close()
                joinAll(*jobs.toTypedArray())
            }
        } catch (e: Exception) {
            processException(e)
        }
    }

    private fun createPipelineData(
        columns: MutableList<Column<EntityID<Int>>>,
        row: ResultRow,
        line: ProductionLine
    ): List<PipelineData> {
        return columns.map { column ->
            val id = row[column]
            var entity = entityCache[id.value]
            if (entity == null) {
                entity = transaction { SourceEntity.findById(id) }
                    ?: throw MediationException("Matched entity<id:${id.value}> can not be found")
                entity.readAttributes()
                entityCache[entity.id.value] = entity
            }

            PipelineData(
                entity,
                line.inputEntities.find {
                    it.entity.name == entity.getEntityClass().type
                }!!
            )
        }
    }

    private fun createTopPipelineSet(line: ProductionLine) = transaction {
        line.inputEntities.forEach {
            val alias = SourceEntities.alias("entity_${it.entity.name}")
            var where = Op.build { alias[SourceEntities.entityType] eq it.entity.name }
            it.filter?.let { filter -> where = where.and(filter.where(alias)) }
            PipelineSets.insert(
                alias
                    .slice(
                        StringConst(line.pipeline.pipelineID),
                        alias[SourceEntities.id],
                        BooleanConst(false)
                    ).select(where)
            )
        }
    }

    private fun mainQuery(line: ProductionLine): Triple<ColumnSet, Op<Boolean>, MutableList<Column<EntityID<Int>>>> {
        val startTable = SourceEntities.alias("start_table")
        var from: ColumnSet = startTable
        var where = Op.build {
            startTable[SourceEntities.entityType] eq line.inputEntities.first().entity.name
        }
        line.inputEntities.first().filter?.let { where = where.and(it.where(startTable)) }
        val columns = mutableListOf(startTable[SourceEntities.id])
        line.inputEntities.drop(1).forEachIndexed { i, entity ->
            val alias = SourceEntities.alias("source_entity_$i")
            from = from.crossJoin(alias)
            where = where.and(Op.build { alias[SourceEntities.entityType] eq entity.entity.name })
            entity.filter?.let { where = where.and(it.where(alias)) }
            columns.add(alias[SourceEntities.id])
        }
        return Triple(from, where, columns)
    }

    private fun processException(e: Exception) {
        logger.error("{}", e.message)
        logger.trace("Caused by:", e)
    }

}

