package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.Entity
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection

@PublicApi
abstract class PipelineAssemblerPlugin : NewPlugin<Map<Attribute, Any?>>() {
    abstract fun assemble(target: Entity, entities: PipelineDataCollection): Map<Attribute, Any?>

    open class Options: NewPlugin.Options()
}

@PublicApi
typealias PipelineAssemblerFunction = (target: Entity, entities: PipelineDataCollection) -> Map<Attribute, Any?>

