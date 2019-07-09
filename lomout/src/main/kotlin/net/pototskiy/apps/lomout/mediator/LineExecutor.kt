package net.pototskiy.apps.lomout.mediator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.MessageBundle.message
import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.AppException
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.config.pipeline.ClassifierElement
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import org.apache.logging.log4j.Logger

abstract class LineExecutor(protected val repository: EntityRepositoryInterface) {
    private lateinit var line: AbstractLine

    protected abstract val logger: Logger
    private val jobs = mutableListOf<Job>()
    protected var processedRows = 0L

    abstract fun processResultData(data: Map<Attribute, Any>): Long
    abstract fun preparePipelineExecutor(line: AbstractLine): PipelineExecutor

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    open fun executeLine(line: AbstractLine): Long {
        this.line = line
        processedRows = 0L
        try {
            runBlocking {
                val pipeline = preparePipelineExecutor(line)
                val inputChannel: Channel<ClassifierElement> = Channel()
                jobs.add(launch(Dispatchers.IO) {
                    pipeline.execute(inputChannel).consumeEach {
                        try {
                            processedRows += processResultData(it)
                        } catch (e: AppException) {
                            processException(e)
                        }
                    }
                })
                topLevelInput(line).forEach { inputChannel.send(it) }
                inputChannel.close()
                joinAll(*jobs.toTypedArray())
            }
        } catch (e: Exception) {
            processException(e)
        }
        return processedRows
    }

    private suspend fun topLevelInput(line: AbstractLine) =
        sequence<ClassifierElement> {
            line.inputEntities.forEach { input ->
                var pageNumber = 0
                do {
                    val items = repository.getIDs(input.entity, PAGE_SIZE, pageNumber, input.includeDeleted)
                    items.forEach {
                        yield(ClassifierElement.Mismatched(repository.get(input.entity, it, input.includeDeleted)!!))
                    }
                    pageNumber++
                } while (items.isNotEmpty())
            }
        }

    private fun processException(e: Exception) {
        when (e) {
            is AppConfigException -> logger.error(
                message("message.error.mediator.entity_cannot_process"),
                e.message,
                e.place.placeInfo()
            )
            is AppDataException -> logger.error(
                message("message.error.mediator.entity_cannot_process"),
                e.message,
                e.place.placeInfo()
            )
            else -> logger.error(message("message.error.mediator.entity_cannot_process_only_msg"), e.message)
        }
        logger.trace(message("message.error.caused"), e)
    }

    companion object {
        const val PAGE_SIZE = 1000
    }
}
