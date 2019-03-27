package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.Type

abstract class PipelineAssemblerPlugin : Plugin() {
    abstract fun assemble(target: EntityType, entities: PipelineDataCollection): Map<AnyTypeAttribute, Type?>
}

typealias PipelineAssemblerFunction =
            PluginContextInterface.(target: EntityType, entities: PipelineDataCollection) -> Map<AnyTypeAttribute, Type?>
