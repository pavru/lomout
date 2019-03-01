package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.EType
import net.pototskiy.apps.magemediation.api.entity.Type

abstract class PipelineAssemblerPlugin : Plugin() {
    abstract fun assemble(target: EType, entities: PipelineDataCollection): Map<AnyTypeAttribute, Type?>
}

typealias PipelineAssemblerFunction = (target: EType, entities: PipelineDataCollection) -> Map<AnyTypeAttribute, Type?>
