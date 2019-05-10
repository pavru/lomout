package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.config.mediator.PipelineDataCollection

/**
 * Base class for any pipeline classifiers
 */
@PublicApi
abstract class PipelineClassifierPlugin : Plugin() {
    /**
     * Classifier function
     *
     * @param entities PipelineDataCollection The pipeline entity collection
     * @return Pipeline.CLASS
     */
    abstract fun classify(entities: PipelineDataCollection): Pipeline.CLASS
}

/**
 * Function type for inline pipeline classifier
 */
typealias PipelineClassifierFunction = PluginContextInterface.(entities: PipelineDataCollection) -> Pipeline.CLASS
