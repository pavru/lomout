package net.pototskiy.apps.magemediation.loader

import kotlinx.coroutines.*
import net.pototskiy.apps.magemediation.api.LOADER_LOG_NAME
import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.source.WorkbookFactory
import org.apache.logging.log4j.LogManager

object DataLoader {
    private val log = LogManager.getLogger(LOADER_LOG_NAME)
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)

    @ObsoleteCoroutinesApi
    fun load(config: Config) = runBlocking {
        statusLog.info("Data loading has started")
        val jobs = mutableListOf<Job>()
        val orderedLoads = config.loader.loads.map { load ->
            load.sources.map { it.file to load }
        }
            .flatten()
            .groupBy { it.first }
        orderedLoads.keys.forEach { file ->
            launch(newSingleThreadContext(file.id)) {
                log.debug("Start loading file<{}>", file.id)
                orderedLoads[file]?.forEach { (_, load) ->
                    val source = load.sources.find { it.file == file }!!
                    WorkbookFactory.create(file.file.toURI().toURL()).use {workbook ->
                        workbook.filter { source.sheet.isMatch(it.name) }.forEach {
                            log.debug("Start loading sheet<{}> from file<{}>", it.name, file.id)
                            EntityLoader(load,source.emptyRowStrategy,it).load()
                            log.debug("Finish loading sheet<{}> from file<{}>", it.name, file.id)
                        }
                    }
                }
                log.debug("Finish loading file<{}>", file.id)
            }.also { jobs.add(it) }
        }
        joinAll(*jobs.toTypedArray())
        statusLog.info("Data loading has finished")
    }
}
