package net.pototskiy.apps.magemediation.mediator

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.Entity
import net.pototskiy.apps.magemediation.api.config.mediator.InputEntityCollection
import net.pototskiy.apps.magemediation.api.config.mediator.Pipeline
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineData
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.database.schema.SourceEntity
import net.pototskiy.apps.magemediation.database.BooleanConst
import net.pototskiy.apps.magemediation.database.PipelineSets
import net.pototskiy.apps.magemediation.database.StringConst
import org.apache.commons.collections4.map.LRUMap
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PipelineExecutor(
    private val pipeline: Pipeline,
    private val inputEntities: InputEntityCollection,
    private val targetEntity: Entity,
    private val entityCache: LRUMap<Int, PersistentSourceEntity>
) {

    private val jobs = mutableListOf<Job>()

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    suspend fun execute(inputData: Channel<PipelineDataCollection>): ReceiveChannel<Map<Attribute, Any?>> =
        GlobalScope.produce {
            val matchedData: Channel<PipelineDataCollection> = Channel()
            val nextMatchedPipe = pipeline.pipelines.find {
                it.isApplicablePipeline(Pipeline.CLASS.MATCHED)
            }?.let { PipelineExecutor(it, inputEntities, targetEntity, entityCache) }

            jobs.add(launch { nextMatchedPipe?.execute(matchedData)?.consumeEach { send(it) } })

            inputData.consumeEach { data ->
                val klass = pipeline.classifier.classify(data)
                if (klass == Pipeline.CLASS.MATCHED) {
                    if (nextMatchedPipe != null) {
                        addToSet(nextMatchedPipe.pipeline.pipelineID, data)
                        matchedData.send(data)
                    } else {
                        val assembler = pipeline.assembler
                            ?: throw ConfigException("Pipeline has no child matched pipeline neither assembler")
                        send(assembler.assemble(targetEntity, data))
                    }
                    markAsMatched(data)
                }
            }
            matchedData.close()

            val unMatchedData: Channel<PipelineDataCollection> = Channel()
            val nextUnMatchedPipe = pipeline.pipelines.find {
                it.isApplicablePipeline(Pipeline.CLASS.UNMATCHED)
            }?.let { PipelineExecutor(it, inputEntities, targetEntity, entityCache) }
            jobs.add(launch { nextUnMatchedPipe?.execute(unMatchedData)?.consumeEach { send(it) } })
            if (nextUnMatchedPipe != null) {
                createChildUmMatchedSet(nextUnMatchedPipe)
                produce<PipelineDataCollection> {
                    val from = PipelineSets
                    val where = Op.build {
                        (PipelineSets.setID eq pipeline.pipelineID) and
                                (PipelineSets.isMatched neq true)
                    }
                    rowSequence(from, where, listOf(PipelineSets.entityID)) {
                        readEntity(it[PipelineSets.entityID])
                    }.forEach { entity ->
                        val inputEntity = inputEntities.find { it.entity.name == entity.getEntityClass().type }
                            ?: throw MediationException("Unexpected input entity<${entity.getEntityClass().type}")
                        unMatchedData.send(PipelineDataCollection(listOf(PipelineData(entity, inputEntity))))
                    }
                }.consumeEach { unMatchedData.send(it) }
            }
            unMatchedData.close()
            joinAll(*jobs.toTypedArray())
        }

    private fun createChildUmMatchedSet(nextUnMatchedPipe: PipelineExecutor) = transaction {
        PipelineSets.insert(
            PipelineSets
                .slice(
                    StringConst(nextUnMatchedPipe.pipeline.pipelineID),
                    PipelineSets.entityID,
                    BooleanConst(false)
                )
                .select {
                    with(PipelineSets) {
                        (setID eq pipeline.pipelineID) and (isMatched neq true)
                    }
                }
        )
    }

    private fun readEntity(id: EntityID<Int>): PersistentSourceEntity {
        var entity = entityCache[id.value]
        if (entity == null) {
            entity = transaction { SourceEntity.findById(id) }
                ?: throw MediationException("Matched entity<id:${id.value}> can not be found")
            entity.readAttributes()
            entityCache[entity.id.value] = entity
        }
        return entity
    }

    private fun addToSet(setID: String, data: PipelineDataCollection) = transaction {
        val alreadyInSet = PipelineSets
            .slice(PipelineSets.entityID)
            .select {
            (PipelineSets.setID eq setID) and
                    (PipelineSets.entityID inList data.map { it.entity.id })
        }.map { it[PipelineSets.entityID] }.toList()
        PipelineSets.batchInsert(data.map { it.entity.id }.minus(alreadyInSet)) { id ->
            this[PipelineSets.setID] = setID
            this[PipelineSets.entityID] = id
            this[PipelineSets.isMatched] = false
        }
    }

    private fun markAsMatched(data: PipelineDataCollection) = transaction {
        PipelineSets.update({
            (PipelineSets.setID eq pipeline.pipelineID) and
                    (PipelineSets.entityID inList data.map { it.entity.id })
        }) { it[isMatched] = true }
    }
}
