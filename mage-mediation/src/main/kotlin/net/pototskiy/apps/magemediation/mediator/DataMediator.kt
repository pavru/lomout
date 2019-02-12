package net.pototskiy.apps.magemediation.mediator

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.database.MediumTmpMatches
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

object DataMediator {

    fun mediate(config: Config) {
        transaction { MediumTmpMatches.deleteAll() }
        val orderedLines = config.mediator.lines.groupBy { it.outputEntity.name }
        orderedLines.forEach{(entityType, lines) ->
            lines.forEach { ProductionLineExecutor().executeLine(it) }
        }
    }
}
