package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.config.mediator.PipelineDataCollection

@PublicApi
abstract class PipelineClassifierPlugin : Plugin() {
    abstract fun classify(entities: PipelineDataCollection): Pipeline.CLASS
}

typealias PipelineClassifierFunction = PluginContextInterface.(entities: PipelineDataCollection) -> Pipeline.CLASS
