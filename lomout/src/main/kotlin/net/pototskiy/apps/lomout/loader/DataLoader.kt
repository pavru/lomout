package net.pototskiy.apps.lomout.loader

import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.MessageBundle.message
import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.*

object DataLoader {
    private val processedRows = AtomicLong(0)
    private val log = LogManager.getLogger(LOADER_LOG_NAME)
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)

    @ObsoleteCoroutinesApi
    fun load(repository: EntityRepositoryInterface, config: Config) = runBlocking {
        val loader = config.loader ?: return@runBlocking
        statusLog.info(message("message.info.loader.started"))
        val startTime = LocalDateTime.now()
        val jobs = mutableListOf<Job>()
        val orderedLoads = loader.loads.map { load ->
            load.sources.map { it.file to load }
        }.flatten().groupBy { it.first }
        orderedLoads.keys.forEach { file ->
            launch(newSingleThreadContext(file.id)) {
                log.debug(message("message.debug.loader.start_file"), file.id)
                orderedLoads[file]?.forEach { (_, load) ->
                    val source = load.sources.find { it.file == file }!!
                    WorkbookFactory.create(file.file.toURI().toURL(), file.locale).use { workbook ->
                        if (!workbook.any { source.sheet.isMatch(it.name) }) {
                            log.warn(
                                message("message.warn.loader.sheet_not_found"),
                                source.file.id, source.sheet.definition
                            )
                        } else {
                            workbook.filter { source.sheet.isMatch(it.name) }.forEach {
                                log.debug(message("message.debug.loader.start_sheet"), it.name, file.id)
                                EntityLoader(repository, load, source.emptyRowBehavior, it).apply {
                                    load()
                                    this@DataLoader.processedRows.addAndGet(processedRows)
                                }
                                log.debug(message("message.debug.loader.finish_sheet"), it.name, file.id)
                            }
                        }
                    }
                }
                log.debug(message("message.debug.loader.finish_file"), file.id)
            }.also { jobs.add(it) }
        }
        @Suppress("SpreadOperator")
        joinAll(*jobs.toTypedArray())
        val duration = Duration.between(startTime, LocalDateTime.now()).seconds
        statusLog.info(message("message.info.loader.finished", duration, processedRows.get()))
    }
}
