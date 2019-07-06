package net.pototskiy.apps.lomout.mediator

import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.*

object DataMediator {
    private const val DEFAULT_MAX_AGE = 10
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    private val log = LogManager.getLogger(PRINTER_LOG_NAME)
    private val processedRows = AtomicLong(0L)

    fun mediate(repository: EntityRepositoryInterface, config: Config) {
        val mediator = config.mediator ?: return
        statusLog.info("Data mediating has started")
        val startTime = LocalDateTime.now()
        val orderedLines = mediator.lines.groupBy { it.outputEntity.qualifiedName }
        orderedLines.forEach { (_, lines) ->
            lines.forEach { line ->
                log.debug("Start creating entity<{}>", line.outputEntity.qualifiedName)
                val eType = line.outputEntity
                val rows = ProductionLineExecutor(repository).executeLine(line)
                repository.markEntitiesAsRemoved(eType)
                repository.updateAbsentDays(eType)
                repository.removeOldEntities(eType, DEFAULT_MAX_AGE)
                processedRows.addAndGet(rows)
                log.debug("Finish creating entity<{}>", line.outputEntity.qualifiedName)
            }
        }
        val duration = Duration.between(startTime, LocalDateTime.now()).seconds
        statusLog.info("Data mediating has finished, duration: ${duration}s, rows: ${processedRows.get()}")
    }
}
