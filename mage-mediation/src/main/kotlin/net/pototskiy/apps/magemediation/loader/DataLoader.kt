package net.pototskiy.apps.magemediation.loader

import kotlinx.coroutines.*
import net.pototskiy.apps.magemediation.api.LOADER_LOG_NAME
import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookFactory
import org.apache.logging.log4j.LogManager
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.atomic.AtomicLong

object DataLoader {
    private var startTime = DateTime()
    private val processedRows = AtomicLong(0)
    private val log = LogManager.getLogger(LOADER_LOG_NAME)
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)

    @ObsoleteCoroutinesApi
    fun load(config: Config) = runBlocking {
        statusLog.info("Data loading has started")
        startTime = DateTime()
        val jobs = mutableListOf<Job>()
        val orderedLoads = config.loader.loads.map { load ->
            load.sources.map { it.file to load }
        }.flatten().groupBy { it.first }
        orderedLoads.keys.forEach { file ->
            launch(newSingleThreadContext(file.id)) {
                log.debug("Start loading file<{}>", file.id)
                orderedLoads[file]?.forEach { (_, load) ->
                    val source = load.sources.find { it.file == file }!!
                    WorkbookFactory.create(file.file.toURI().toURL(), file.locale).use { workbook ->
                        workbook.filter { source.sheet.isMatch(it.name) }.forEach {
                            log.debug("Start loading sheet<{}> from file<{}>", it.name, file.id)
                            EntityLoader(load, source.emptyRowStrategy, it).apply {
                                load()
                                this@DataLoader.processedRows.addAndGet(processedRows)
                            }
                            log.debug("Finish loading sheet<{}> from file<{}>", it.name, file.id)
                        }
                    }
                }
                log.debug("Finish loading file<{}>", file.id)
            }.also { jobs.add(it) }
        }
        joinAll(*jobs.toTypedArray())
        val duration = Duration(startTime, DateTime()).millis.toDouble() / 1000.0
        statusLog.info("Data loading has finished, duration: ${duration}s, rows: ${processedRows.get()}")
    }
}
