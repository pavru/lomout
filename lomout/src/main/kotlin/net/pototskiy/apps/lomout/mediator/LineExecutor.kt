package net.pototskiy.apps.lomout.mediator

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.AppException
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine.LineType
import net.pototskiy.apps.lomout.api.config.mediator.PipelineData
import net.pototskiy.apps.lomout.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.database.BooleanConst
import net.pototskiy.apps.lomout.database.PipelineSets
import net.pototskiy.apps.lomout.database.StringConst
import org.apache.commons.collections4.map.LRUMap
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

abstract class LineExecutor(
    val entityTypeManager: EntityTypeManager,
    cacheSizeProperty: Int
) {

    @Suppress("MagicNumber")
    private val maxCacheSize = if (cacheSizeProperty == 0) 1000 else cacheSizeProperty
    @Suppress("MagicNumber")
    private val initialCacheSize = if (cacheSizeProperty == 0) 300 else cacheSizeProperty / 2

    protected val pipelineDataCache = LRUMap<Int, PipelineData>(maxCacheSize, initialCacheSize)
    protected abstract val logger: Logger
    private val jobs = mutableListOf<Job>()
    protected var processedRows = 0L

    abstract fun processResultData(data: Map<AnyTypeAttribute, Type?>): Long
    abstract fun preparePipelineExecutor(line: AbstractLine): PipelineExecutor

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    open fun executeLine(line: AbstractLine): Long {
        processedRows = 0L
        try {
            runBlocking {
                val pipeline = preparePipelineExecutor(line)
                createTopPipelineSet(line)
                val (from, where, columns) = if (line.lineType == LineType.CROSS) {
                    crossMainQuery(line)
                } else {
                    unionMainQuery(line)
                }
                val inputChannel: Channel<PipelineDataCollection> = Channel()
                jobs.add(launch {
                    pipeline.execute(inputChannel).consumeEach {
                        try {
                            processedRows += processResultData(it)
                        } catch (e: AppException) {
                            logger.error("Can not print entity, {}", e.message)
                            logger.trace("Cause:", e)
                        }
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
        return processedRows
    }

    private fun createPipelineData(
        columns: MutableList<Column<EntityID<Int>>>,
        row: ResultRow,
        line: AbstractLine
    ): List<PipelineData> {
        return columns.map { column ->
            val id = row[column]
            val pipelineData = pipelineDataCache[id.value]
            if (pipelineData == null) {
                val entity = transaction { DbEntity.findById(id) }
                    ?: throw AppDataException("Matched entity<id:${id.value}> can not be found")
                entity.readAttributes()
                PipelineData(
                    entityTypeManager,
                    entity,
                    line.inputEntities.find {
                        it.entity.name == entity.eType.name
                    }!!
                ).also { pipelineDataCache[id.value] = it }
            } else {
                pipelineData
            }
        }
    }

    private fun createTopPipelineSet(line: AbstractLine) = transaction {
        line.inputEntities.forEach {
            val alias = DbEntityTable.alias("entity_${it.entity.name}")
            var where = Op.build { alias[DbEntityTable.entityType] eq it.entity }
            it.filter?.let { filter -> where = where.and(filter.where(alias)) }
            PipelineSets.insert(
                alias
                    .slice(
                        StringConst(line.pipeline.pipelineID),
                        alias[DbEntityTable.id],
                        BooleanConst(false)
                    ).select(where)
            )
        }
    }

    private fun crossMainQuery(line: AbstractLine):
            Triple<ColumnSet, Op<Boolean>, MutableList<Column<EntityID<Int>>>> {
        val startTable = DbEntityTable.alias("start_table")
        var from: ColumnSet = startTable
        var where = Op.build {
            startTable[DbEntityTable.entityType] eq line.inputEntities.first().entity
        }
        line.inputEntities.first().filter?.let { where = where.and(it.where(startTable)) }
        val columns = mutableListOf(startTable[DbEntityTable.id])
        line.inputEntities.drop(1).forEachIndexed { i, entity ->
            val alias = DbEntityTable.alias("source_entity_$i")
            from = from.crossJoin(alias)
            where = where.and(Op.build { alias[DbEntityTable.entityType] eq entity.entity })
            entity.filter?.let { where = where.and(it.where(alias)) }
            columns.add(alias[DbEntityTable.id])
        }
        return Triple(from, where, columns)
    }

    private fun unionMainQuery(line: AbstractLine):
            Triple<ColumnSet, Op<Boolean>, MutableList<Column<EntityID<Int>>>> {
        val from: ColumnSet = PipelineSets
        val where = Op.build { PipelineSets.setID eq line.pipeline.pipelineID }
        val columns = mutableListOf(PipelineSets.entityID)
        return Triple(from, where, columns)
    }

    private fun processException(e: Exception) {
        logger.error("{}", e.message)
        logger.trace("Caused by:", e)
    }
}
