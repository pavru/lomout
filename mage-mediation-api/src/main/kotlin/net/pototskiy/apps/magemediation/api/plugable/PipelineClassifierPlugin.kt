package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.mediator.Pipeline
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection

@PublicApi
abstract class PipelineClassifierPlugin : Plugin() {
    abstract fun classify(entities: PipelineDataCollection): Pipeline.CLASS
}

typealias PipelineClassifierFunction = (entities: PipelineDataCollection) -> Pipeline.CLASS

