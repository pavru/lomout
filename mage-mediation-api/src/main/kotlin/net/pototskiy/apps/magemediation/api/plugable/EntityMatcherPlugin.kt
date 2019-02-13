package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.mediator.MatcherEntityData


abstract class EntityMatcherPlugin : NewPlugin<Boolean>() {
    protected lateinit var entities: Map<String, MatcherEntityData>

    fun matches(entities: Map<String, MatcherEntityData>): Boolean {
        this.entities = entities
        return execute()
    }

    open class Options: NewPlugin.Options()
}

typealias EntityMatcherFunction = (entities: Map<String, MatcherEntityData>) -> Boolean

