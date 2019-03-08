package net.pototskiy.apps.magemediation.mediator

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.mediator.ProductionLine.LineType.CROSS
import net.pototskiy.apps.magemediation.api.config.mediator.ProductionLine.LineType.UNION
import net.pototskiy.apps.magemediation.database.PipelineSets
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

object DataMediator {

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    fun mediate(config: Config) {
        transaction { PipelineSets.deleteAll() }
        val orderedLines = config.mediator.lines.groupBy { it.outputEntity.name }
        orderedLines.forEach { (_, lines) ->
            lines.forEach {
                when (it.lineType) {
                    CROSS -> CrossProductionLineExecutor(config.entityTypeManager).executeLine(it)
                    UNION -> UnionProductionLineExecutor(config.entityTypeManager).executeLine(it)
                }
            }
        }
    }
}
