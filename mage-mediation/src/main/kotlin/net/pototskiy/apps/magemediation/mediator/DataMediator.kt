package net.pototskiy.apps.magemediation.mediator

import net.pototskiy.apps.magemediation.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.database.SourceEntities
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DataMediator {

    fun mediate(config: Config) {
        Configurator.setLevel(EXPOSED_LOG_NAME, Level.TRACE)
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
        val count = transaction { from.slice(columns).select { where }.count() }
        println("combinations: $count")
        for (page in 0..count / 100 + if (count % 100 != 0) 1 else 0) {
            val start = page * 100
            transaction {
                from
                    .slice(columns)
                    .select { where }
                    .limit(100, start)
                    .toList()
            }.forEach {
                println("${it[columns[0]]},${it[columns[1]]}")
            }
        }
    }
}
