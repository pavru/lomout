package net.pototskiy.apps.lomout.mediator

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.AppException
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.config.pipeline.ClassifierElement
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.EntityCollection
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.entity.type.Type
import org.apache.logging.log4j.Logger

abstract class LineExecutor(protected val repository: EntityRepositoryInterface) {
    private lateinit var line: AbstractLine

    protected abstract val logger: Logger
    private val jobs = mutableListOf<Job>()
    protected var processedRows = 0L

    abstract fun processResultData(data: Map<AnyTypeAttribute, Type>): Long
    abstract fun preparePipelineExecutor(line: AbstractLine): PipelineExecutor

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    open fun executeLine(line: AbstractLine): Long {
        addExtensionAttributes(line)
        this.line = line
        processedRows = 0L
        try {
            runBlocking {
                val pipeline = preparePipelineExecutor(line)
                val inputChannel: Channel<ClassifierElement> = Channel()
                jobs.add(launch {
                    pipeline.execute(inputChannel).consumeEach {
                        try {
                            processedRows += processResultData(it)
                        } catch (e: AppException) {
                            logger.error("Cannot process entity, {}", e.message)
                            logger.trace("Cause: ", e)
                        }
                    }
                })
                topLevelInput(line).forEach { inputChannel.send(it) }
                inputChannel.close()
                joinAll(*jobs.toTypedArray())
            }
        } catch (e: Exception) {
            processException(e)
        } finally {
            removeExtensionAttributes(line)
        }
        return processedRows
    }

    private fun addExtensionAttributes(line: AbstractLine) {
        line.inputEntities.forEach { input ->
            input.extAttributes.forEach {
                repository.entityTypeManager.addEntityExtAttribute(input.entity, it)
            }
        }
    }

    private fun removeExtensionAttributes(line: AbstractLine) {
        line.inputEntities.forEach { input ->
            input.extAttributes.forEach {
                repository.entityTypeManager.removeEntityExtAttribute(input.entity, it)
            }
        }
    }

    private suspend fun topLevelInput(line: AbstractLine) =
        sequence<ClassifierElement> {
            line.inputEntities.forEach { input ->
                var pageNumber = 0
                do {
                    @Suppress("SpreadOperator")
                    val items = repository.getIDs(input.entity, PAGE_SIZE, pageNumber, *input.statuses)
                    items.forEach {
                        @Suppress("SpreadOperator")
                        yield(
                            ClassifierElement.Mismatched(
                                EntityCollection(listOf(repository.get(it, *input.statuses)!!))
                            )
                        )
                    }
                    pageNumber++
                } while (items.isNotEmpty())
            }
        }

    private fun processException(e: Exception) {
        logger.error("{}", e.message)
        logger.trace("Caused by:", e)
    }

    companion object {
        const val PAGE_SIZE = 1000
    }
}
