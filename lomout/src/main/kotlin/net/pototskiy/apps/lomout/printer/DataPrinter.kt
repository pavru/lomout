package net.pototskiy.apps.lomout.printer

import net.pototskiy.apps.lomout.MessageBundle.message
import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.*

object DataPrinter {
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    private val printedRows = AtomicLong(0L)
    private val log = LogManager.getLogger(PRINTER_LOG_NAME)

    fun print(repository: EntityRepositoryInterface, config: Config) {
        val printer = config.printer ?: return
        statusLog.info(message("message.info.printer.started"))
        val startTime = LocalDateTime.now()
        val orderedLines = printer.lines.groupBy { it.outputFieldSets.file.file.id }
        orderedLines.forEach { (_, lines) ->
            lines.forEach { line ->
                log.debug(message("message.debug.property.start_file", line.outputFieldSets.file.file.file.name))
                val rows = PrinterLineExecutor(repository).executeLine(line)
                printedRows.addAndGet(rows)
                log.debug(message("message.debug.printer.finish_file", line.outputFieldSets.file.file.file.name))
            }
        }
        val duration = Duration.between(startTime, LocalDateTime.now()).seconds
        statusLog.info(message("message.info.printer.finished", duration, printedRows.get()))
    }
}
