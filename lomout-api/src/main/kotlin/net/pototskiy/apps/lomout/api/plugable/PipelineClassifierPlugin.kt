package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.config.pipeline.ClassifierElement

/**
 * Base class for any pipeline classifiers
 */
@PublicApi
abstract class PipelineClassifierPlugin : Plugin() {
    /**
     * Classifier function
     *
     * @param element ClassifierElement The element to classify
     * @return ClassifierElement
     */
    abstract fun classify(element: ClassifierElement): ClassifierElement
}

/**
 * Function type for inline pipeline classifier
 */
typealias PipelineClassifierFunction = PluginContextInterface.(element: ClassifierElement) -> ClassifierElement
