package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.plugable.EntityMatcherFunction
import net.pototskiy.apps.magemediation.api.plugable.EntityMatcherPlugin
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class EntityMatcher {
    fun matches(entities: Map<String, MatcherEntityData>): Boolean {
        return when (this) {
            is EntityMatcherWithPlugin -> pluginClass.createInstance().let {
                it.setOptions(options)
                it.matches(entities)
            }
            is EntityMatcherWithFunction -> function(entities)
        }
    }
}

class EntityMatcherWithPlugin(
    val pluginClass: KClass<out EntityMatcherPlugin>,
    val options: NewPlugin.Options = NewPlugin.noOptions
) : EntityMatcher()

class EntityMatcherWithFunction(val function: EntityMatcherFunction) : EntityMatcher()
