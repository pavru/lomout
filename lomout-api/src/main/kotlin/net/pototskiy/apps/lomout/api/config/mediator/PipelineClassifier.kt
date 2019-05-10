package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.plugable.PipelineClassifierFunction
import net.pototskiy.apps.lomout.api.plugable.PipelineClassifierPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Abstract pipeline classifier
 */
sealed class PipelineClassifier {
    /**
     * Classifier function
     *
     * @param entities PipelineDataCollection The pipeline input entities
     * @return Pipeline.CLASS
     */
    fun classify(entities: PipelineDataCollection): Pipeline.CLASS {
        return when (this) {
            is PipelineClassifierWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.classify(entities)
            }
            is PipelineClassifierWithFunction -> PluginContext.function(entities)
        }
    }
}

/**
 * Pipeline classifier with plugin
 *
 * @property pluginClass KClass<out PipelineClassifierPlugin> The classifier plugin class
 * @property options PipelineClassifierPlugin.() -> Unit The classifier options
 * @constructor
 */
class PipelineClassifierWithPlugin(
    val pluginClass: KClass<out PipelineClassifierPlugin>,
    val options: PipelineClassifierPlugin.() -> Unit = {}
) : PipelineClassifier()

/**
 * Pipeline classifier with function
 *
 * @property function PipelineClassifierFunction The classifier function
 * @constructor
 */
class PipelineClassifierWithFunction(val function: PipelineClassifierFunction) : PipelineClassifier()
