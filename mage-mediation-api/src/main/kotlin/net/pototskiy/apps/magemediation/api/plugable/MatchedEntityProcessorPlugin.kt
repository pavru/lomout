package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.mediator.MatcherEntityData

abstract class MatchedEntityProcessorPlugin : NewPlugin<Map<Attribute, Any?>>() {
    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var entities: Map<String, MatcherEntityData>

    fun process(entities: Map<String, MatcherEntityData>): Map<Attribute, Any?> {
        this.entities = entities
        return execute()
    }

    open class Options: NewPlugin.Options()
}

typealias MatchedEntityProcessorFunction = (entities: Map<String, MatcherEntityData>) -> Map<Attribute, Any?>
