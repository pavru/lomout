package net.pototskiy.apps.magemediation.printer

import net.pototskiy.apps.magemediation.api.PRINTER_LOG_NAME
import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.database.PipelineSets
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.atomic.*

object DataPrinter {
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    private var startTime = DateTime()
    private val printedRows = AtomicLong(0L)
    private val log = LogManager.getLogger(PRINTER_LOG_NAME)

    private const val millisInSecond: Double = 1000.0

    fun print(config: Config) {
        transaction { PipelineSets.deleteAll() }
        val printer = config.printer ?: return
        statusLog.info("Data printing has started")
        startTime = DateTime()
        val orderedLines = printer.lines.groupBy { it.outputFieldSets.file.file.id }
        orderedLines.forEach { (_, lines) ->
            lines.forEach { line ->
                log.debug("Start printing file<${line.outputFieldSets.file.file.file.name}>")
                val rows = PrinterLineExecutor(config.entityTypeManager).executeLine(line)
                printedRows.addAndGet(rows)
                log.debug("Finish printing file<${line.outputFieldSets.file.file.file.name}>")
            }
        }
        val duration = Duration(startTime, DateTime()).millis.toDouble() / millisInSecond
        statusLog.info("Data printing has finished, duration: ${duration}s, rows: ${printedRows.get()}")
    }
}
