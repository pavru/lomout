package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.Type

abstract class PipelineAssemblerPlugin : Plugin() {
    abstract fun assemble(target: EntityType, entities: PipelineDataCollection): Map<AnyTypeAttribute, Type?>
}

typealias PipelineAssemblerFunction =
            (target: EntityType, entities: PipelineDataCollection) -> Map<AnyTypeAttribute, Type?>
