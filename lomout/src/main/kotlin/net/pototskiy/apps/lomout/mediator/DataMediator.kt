package net.pototskiy.apps.lomout.mediator

import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.database.PipelineSets
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.atomic.*

object DataMediator {
    private const val DEFAULT_MAX_AGE = 10
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    private val log = LogManager.getLogger(PRINTER_LOG_NAME)
    private val processedRows = AtomicLong(0L)
    private const val millisInSecond: Double = 1000.0

    fun mediate(config: Config) {
        transaction { PipelineSets.deleteAll() }
        val mediator = config.mediator ?: return
        statusLog.info("Data mediating has started")
        val startTime = DateTime()
        val orderedLines = mediator.lines.groupBy { it.outputEntity.name }
        orderedLines.forEach { (_, lines) ->
            lines.forEach {
                log.debug("Start creating entity<{}>", it.outputEntity.name)
                val eType = it.outputEntity
                DbEntity.resetTouchFlag(eType)
                val rows = ProductionLineExecutor(config.entityTypeManager).executeLine(it)
                DbEntity.markEntitiesAsRemove(eType)
                DbEntity.updateAbsentAge(eType)
                DbEntity.removeOldEntities(eType, DEFAULT_MAX_AGE)
                processedRows.addAndGet(rows)
                log.debug("Finish creating entity<{}>", it.outputEntity.name)
            }
        }
        val duration = Duration(startTime, DateTime()).millis.toDouble() / millisInSecond
        statusLog.info("Data mediating has finished, duration: ${duration}s, rows: ${processedRows.get()}")
    }
}
