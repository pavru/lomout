package net.pototskiy.apps.magemediation.printer

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.magemediation.api.AppDataException
import net.pototskiy.apps.magemediation.api.AppException
import net.pototskiy.apps.magemediation.api.MEDIATOR_LOG_NAME
import net.pototskiy.apps.magemediation.api.PRINTER_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineData
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.magemediation.api.config.printer.PrinterLine
import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.database.BooleanConst
import net.pototskiy.apps.magemediation.database.PipelineSets
import net.pototskiy.apps.magemediation.database.StringConst
import net.pototskiy.apps.magemediation.mediator.PipelineExecutor
import net.pototskiy.apps.magemediation.mediator.rowSequence
import org.apache.commons.collections4.map.LRUMap
import org.apache.logging.log4j.LogManager
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

class PrinterLineExecutor(val entityTypeManager: EntityTypeManager) {

    private val pipelineDataCache = LRUMap<Int, PipelineData>(maxCacheSize, initialCacheSize)
    private val logger = LogManager.getLogger(MEDIATOR_LOG_NAME)
    private val jobs = mutableListOf<Job>()
    private var printedRows = 0L
    private val log = LogManager.getLogger(PRINTER_LOG_NAME)

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    fun executeLine(line: PrinterLine): Long {
        try {
            runBlocking {
                val entityPrinter = EntityPrinter(
                    line.outputFieldSets.file,
                    line.outputFieldSets.fieldSets,
                    line.outputFieldSets.printHead
                )
                entityPrinter.use { printer ->
                    @Suppress("UNCHECKED_CAST")
                    val pipeline = PipelineExecutor(
                        entityTypeManager,
                        line.pipeline,
                        line.inputEntities,
                        line.inputEntities.first().entity,
                        pipelineDataCache
                    )
                    createTopPipelineSet(line)
                    val (from, where, columns) = mainQuery(line)
                    val inputChannel: Channel<PipelineDataCollection> = Channel()
                    jobs.add(launch {
                        pipeline.execute(inputChannel).consumeEach {
                            try {
                                printedRows += printer.print(it)
                            } catch (e: AppException) {
                                log.error("Can not print entity, {}", e.message)
                                log.trace("Cause:", e)
                            }
                        }
                    })
                    rowSequence(from, where, columns) { row ->
                        createPipelineData(columns, row, line)
                    }.forEach { inputChannel.send(PipelineDataCollection(it)) }
                    inputChannel.close()
                    joinAll(*jobs.toTypedArray())
                }
            }
        } catch (e: Exception) {
            processException(e)
        }
        return printedRows
    }

    private fun createPipelineData(
        columns: MutableList<Column<EntityID<Int>>>,
        row: ResultRow,
        line: PrinterLine
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

    private fun createTopPipelineSet(line: PrinterLine) = transaction {
        line.inputEntities.first().let {
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

    private fun mainQuery(line: PrinterLine):
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

    companion object {
        private val cacheSizeProperty = System.getProperty("printer.line.cache.size").toIntOrNull() ?: 0
        private val maxCacheSize = if (cacheSizeProperty == 0) 1000 else cacheSizeProperty
        private val initialCacheSize = if (cacheSizeProperty == 0) 300 else cacheSizeProperty / 2
    }
}
