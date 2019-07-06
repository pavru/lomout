package net.pototskiy.apps.lomout.mediator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import net.pototskiy.apps.lomout.api.config.mediator.InputEntityCollection
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.config.pipeline.ClassifierElement
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import kotlin.reflect.KClass

class PipelineExecutor(
    private val pipeline: Pipeline,
    private val inputEntities: InputEntityCollection,
    private val targetEntity: KClass<out Document>
) {

    private val jobs = mutableListOf<Job>()

    suspend fun execute(inputData: Channel<ClassifierElement>): ReceiveChannel<Map<Attribute, Any>> =
        GlobalScope.produce {
            val matchedData: Channel<ClassifierElement> = Channel()
            val nextMatchedPipe = pipeline.pipelines.find {
                it.isApplicablePipeline(Pipeline.CLASS.MATCHED)
            }?.let { PipelineExecutor(it, inputEntities, targetEntity) }
            jobs.add(launch { nextMatchedPipe?.execute(matchedData)?.consumeEach { send(it) } })

            val unMatchedData: Channel<ClassifierElement> = Channel()
            val nextUnMatchedPipe = pipeline.pipelines.find {
                it.isApplicablePipeline(Pipeline.CLASS.UNMATCHED)
            }?.let { PipelineExecutor(it, inputEntities, targetEntity) }
            jobs.add(launch { nextUnMatchedPipe?.execute(unMatchedData)?.consumeEach { send(it) } })

            inputData.consumeEach { data ->
                when (val element = pipeline.classifier(data)) {
                    is ClassifierElement.Matched -> {
                        if (nextMatchedPipe != null) {
                            matchedData.send(element)
                        } else {
                            val assembler = pipeline.assembler!!
                            send(assembler(targetEntity, element.entities))
                        }
                    }
                    is ClassifierElement.Skipped -> {
                        // just drop element
                    }
                    else -> if (nextUnMatchedPipe != null) {
                        unMatchedData.send(element)
                    }
                }
            }
            matchedData.close()
            unMatchedData.close()

            @Suppress("SpreadOperator")
            joinAll(*jobs.toTypedArray())
        }
}
