package net.pototskiy.apps.lomout.mediator

import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import org.apache.logging.log4j.LogManager
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.atomic.*

object DataMediator {
    private const val DEFAULT_MAX_AGE = 10
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    private val log = LogManager.getLogger(PRINTER_LOG_NAME)
    private val processedRows = AtomicLong(0L)
    private const val millisInSecond: Double = 1000.0

    fun mediate(repository: EntityRepositoryInterface, config: Config) {
        val mediator = config.mediator ?: return
        statusLog.info("Data mediating has started")
        val startTime = DateTime()
        repository.cacheStrategy = EntityRepositoryInterface.CacheStrategy.MEDIATOR
        val orderedLines = mediator.lines.groupBy { it.outputEntity.name }
        orderedLines.forEach { (_, lines) ->
            lines.forEach { line ->
                log.debug("Start creating entity<{}>", line.outputEntity.name)
                val eType = line.outputEntity
                repository.resetTouchFlag(eType)
                val rows = ProductionLineExecutor(repository).executeLine(line)
                repository.markEntitiesAsRemoved(eType)
                repository.updateAbsentDays(eType)
                repository.removeOldEntities(eType, DEFAULT_MAX_AGE)
                processedRows.addAndGet(rows)
                log.debug("Finish creating entity<{}>", line.outputEntity.name)
            }
        }
        val duration = Duration(startTime, DateTime()).millis.toDouble() / millisInSecond
        statusLog.info("Data mediating has finished, duration: ${duration}s, rows: ${processedRows.get()}")
    }
}
