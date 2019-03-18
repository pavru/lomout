package net.pototskiy.apps.lomout.loader

import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import org.apache.logging.log4j.LogManager
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.atomic.*

object DataLoader {
    private const val millisInSecond = 1000.0
    private var startTime = DateTime()
    private val processedRows = AtomicLong(0)
    private val log = LogManager.getLogger(LOADER_LOG_NAME)
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)

    @ObsoleteCoroutinesApi
    fun load(config: Config) = runBlocking {
        val loader = config.loader ?: return@runBlocking
        statusLog.info("Data loading has started")
        startTime = DateTime()
        val jobs = mutableListOf<Job>()
        val orderedLoads = loader.loads.map { load ->
            load.sources.map { it.file to load }
        }.flatten().groupBy { it.first }
        orderedLoads.keys.forEach { file ->
            launch(newSingleThreadContext(file.id)) {
                log.debug("Start loading file<{}>", file.id)
                orderedLoads[file]?.forEach { (_, load) ->
                    val source = load.sources.find { it.file == file }!!
                    WorkbookFactory.create(file.file.toURI().toURL(), file.locale).use { workbook ->
                        if (!workbook.any { source.sheet.isMatch(it.name) }) {
                            log.warn(
                                "Source<{}> does not contain any sheet matched to name<{}>",
                                source.file.id, source.sheet.definition
                            )
                        } else {
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
                }
                log.debug("Finish loading file<{}>", file.id)
            }.also { jobs.add(it) }
        }
        @Suppress("SpreadOperator")
        joinAll(*jobs.toTypedArray())
        val duration = Duration(startTime, DateTime()).millis.toDouble() / millisInSecond
        statusLog.info("Data loading has finished, duration: ${duration}s, rows: ${processedRows.get()}")
    }
}
