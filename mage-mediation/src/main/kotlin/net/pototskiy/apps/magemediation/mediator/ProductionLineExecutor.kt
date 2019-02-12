package net.pototskiy.apps.magemediation.mediator

import net.pototskiy.apps.magemediation.api.config.mediator.MatcherEntityData
import net.pototskiy.apps.magemediation.api.config.mediator.ProductionLine
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.database.MediumTmpMatch
import net.pototskiy.apps.magemediation.database.MediumTmpMatches
import net.pototskiy.apps.magemediation.database.SourceEntities
import net.pototskiy.apps.magemediation.database.SourceEntity
import org.apache.commons.collections4.map.LRUMap
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ProductionLineExecutor {

    private val entityCache = LRUMap<Int, PersistentSourceEntity>(1000)

    fun executeLine(line: ProductionLine) {
        transaction {
            // TODO: 11.02.2019 May it's necessary to add config control flag to clean up target entity
            SourceEntities.deleteWhere { SourceEntities.entityType eq line.outputEntity.name }
        }
        var from: ColumnSet = SourceEntities
        var where = Op.build { SourceEntities.entityType eq line.inputEntities.first().entity.name }
        val columns = mutableListOf(SourceEntities.id)
        line.inputEntities.drop(1).map { it.entity.name }.forEachIndexed { i, entity ->
            val alias = SourceEntities.alias("source_entity_$i")
            from = from.crossJoin(alias)
            where = where.and(Op.build { alias[SourceEntities.entityType] eq entity })
            columns.add(alias[SourceEntities.id])
        }
        pagedProcess(from, columns, where) { row ->
            val entities = mutableMapOf<String, MatcherEntityData>()
            columns.forEach {
                val id = row[it]
                var entity = entityCache[id.value]
                if (entity == null) {
                    entity = transaction { SourceEntity.findById(id) }
                        ?: throw MediationException("Matched entity<id:${id.value}> can not be found")
                    entity.readAttributes()
                    entityCache[entity.id.value] = entity
                }
                val mappedData = line.inputEntities.mapEntityData(entity)
                entities[entity.entityType] = MatcherEntityData(entity, entity.data, mappedData)
            }
            if (line.matcher.matches(entities)) {
                columns.forEach { insertMatch(line.outputEntity.name, row[it]) }
                line.processors.getMatchedProcessor()?.process(entities)
            }
        }
        processUnMatched(line)
    }

    private fun processUnMatched(line: ProductionLine) {
        val from: ColumnSet = SourceEntities.join(
            MediumTmpMatches,
            JoinType.LEFT,
            SourceEntities.id,
            MediumTmpMatches.matchedEntity
        ) { MediumTmpMatches.target eq line.outputEntity.name }
        val where = Op.build {
            (SourceEntities.entityType inList line.inputEntities.map { it.entity.name }) and
                    (MediumTmpMatches.id.isNull())
        }
        pagedProcess(from, listOf(SourceEntities.id), where) { row ->
            val id = row[SourceEntities.id]
            val entity = transaction { SourceEntity.findById(id) }
                ?: throw MediationException("Entity<id:${id.value}> has not been found")
            entity.readAttributes()
            line.processors.getUnMatchedProcessor(entity.entityType)?.process(entity)
        }
    }

    private fun insertMatch(target: String, id: EntityID<Int>) {
        transaction {
            if (MediumTmpMatch.find {
                    (MediumTmpMatches.target eq target) and
                            (MediumTmpMatches.matchedEntity eq id)
                }.count() == 0) {
                MediumTmpMatch.new {
                    this.target = target
                    this.matchedEntity = id
                }
            }
        }
    }

    private fun pagedProcess(
        from: ColumnSet,
        slice: List<Expression<*>>,
        where: Op<Boolean>,
        block: (row: ResultRow) -> Unit
    ) {
        val count = transaction { from.select { where }.count() }
        for (page in 0..count / PAGE_SIZE) {
            transaction {
                from.slice(slice).select { where }.limit(PAGE_SIZE, page * PAGE_SIZE).toList()
            }.forEach(block)
        }
    }

    companion object {
        private const val PAGE_SIZE = 100
    }
}
