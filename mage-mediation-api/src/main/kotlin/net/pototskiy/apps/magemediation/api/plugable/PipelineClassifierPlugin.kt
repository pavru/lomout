package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.mediator.Pipeline
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection

@PublicApi
abstract class PipelineClassifierPlugin : NewPlugin<Pipeline.CLASS>() {
    protected lateinit var entities: PipelineDataCollection

    @PublicApi
    fun classify(entities: PipelineDataCollection): Pipeline.CLASS {
        this.entities = entities
        return execute()
    }

    open class Options: NewPlugin.Options()
}

@PublicApi
typealias PipelineClassifierFunction = (entities: PipelineDataCollection) -> Pipeline.CLASS

