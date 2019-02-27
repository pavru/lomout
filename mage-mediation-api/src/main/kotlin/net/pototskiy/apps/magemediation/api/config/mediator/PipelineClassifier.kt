package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.plugable.PipelineClassifierFunction
import net.pototskiy.apps.magemediation.api.plugable.PipelineClassifierPlugin
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class PipelineClassifier {
    fun classify(entities: PipelineDataCollection): Pipeline.CLASS {
        return when (this) {
            is PipelineClassifierWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.classify(entities)
            }
            is PipelineClassifierWithFunction -> function(entities)
        }
    }
}

class PipelineClassifierWithPlugin(
    val pluginClass: KClass<out PipelineClassifierPlugin>,
    val options: PipelineClassifierPlugin.() -> Unit = {}
) : PipelineClassifier()

class PipelineClassifierWithFunction(val function: PipelineClassifierFunction) : PipelineClassifier()
