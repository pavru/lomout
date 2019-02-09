package net.pototskiy.apps.magemediation.mediator

import net.pototskiy.apps.magemediation.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.database.MediumTmpMatch
import net.pototskiy.apps.magemediation.database.MediumTmpMatches
import net.pototskiy.apps.magemediation.database.SourceEntities
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DataMediator {

    const val PAGE_SIZE = 100

    fun mediate(config: Config) {
        Configurator.setLevel(EXPOSED_LOG_NAME, Level.TRACE)
        transaction { MediumTmpMatches.deleteAll() }
        val entities = listOf("onec-product", "mage-product")
        var from: ColumnSet = SourceEntities
        var where = Op.build { SourceEntities.entityType eq entities.first() }
        val columns = mutableListOf(SourceEntities.id)
        entities.drop(1).forEachIndexed { i, entity ->
            val alias = SourceEntities.alias("source_entity_$i")
            from = from.crossJoin(alias)
            where = where.and(Op.build { alias[SourceEntities.entityType] eq entity })
            columns.add(alias[SourceEntities.id])
        }
        pagedProcess(from, columns, where) {
            val v1 = it[columns[0]]
            val v2 = it[columns[1]]
            if (v2.value % v1.value == 5) {
                insertMatch("target", v1)
                insertMatch("target", v2)
            }
        }
        processMatched()
        processUnMatched(entities)
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

    private fun processMatched() {
        val from: ColumnSet = SourceEntities.innerJoin(MediumTmpMatches)
        val where = Op.build { MediumTmpMatches.target eq "target" }
        println("matched")
        pagedProcess(from, SourceEntities.columns, where) { println(it[SourceEntities.id]) }
    }

    private fun processUnMatched(entities: List<String>) {
        val from: ColumnSet = SourceEntities.join(
            MediumTmpMatches,
            JoinType.LEFT,
            SourceEntities.id,
            MediumTmpMatches.matchedEntity
        ) { MediumTmpMatches.target eq "target" }
        val where = Op.build {
            (SourceEntities.entityType inList entities) and
                    (MediumTmpMatches.id.isNull())
        }
        println("unmatched")
        pagedProcess(from, SourceEntities.columns, where) { println(it[SourceEntities.id]) }
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
}
