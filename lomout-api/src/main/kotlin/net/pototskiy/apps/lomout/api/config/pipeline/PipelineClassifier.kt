package net.pototskiy.apps.lomout.api.config.pipeline

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
     * @param element ClassifierElement Element to classify
     * @return ClassifierElement
     */
    operator fun invoke(element: ClassifierElement): ClassifierElement {
        return when (this) {
            is PipelineClassifierWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.classify(element)
            }
            is PipelineClassifierWithFunction -> PluginContext.function(element)
        }
    }
}

/**
 * Pipeline classifier with a plugin
 *
 * @property pluginClass The classifier plugin class
 * @property options The classifier options
 * @constructor
 */
class PipelineClassifierWithPlugin(
    val pluginClass: KClass<out PipelineClassifierPlugin>,
    val options: PipelineClassifierPlugin.() -> Unit = {}
) : PipelineClassifier()

/**
 * Pipeline classifier with function
 *
 * @property function The classifier function
 * @constructor
 */
class PipelineClassifierWithFunction(val function: PipelineClassifierFunction) : PipelineClassifier()
