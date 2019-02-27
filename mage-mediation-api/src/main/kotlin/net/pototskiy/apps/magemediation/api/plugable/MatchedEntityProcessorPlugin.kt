package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.mediator.MatcherEntityData
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.Type

abstract class MatchedEntityProcessorPlugin : Plugin() {
    abstract fun process(entities: Map<String, MatcherEntityData>): Map<AnyTypeAttribute, Type?>
}

typealias MatchedEntityProcessorFunction =
            (entities: Map<String, MatcherEntityData>) -> Map<Type, Type?>
