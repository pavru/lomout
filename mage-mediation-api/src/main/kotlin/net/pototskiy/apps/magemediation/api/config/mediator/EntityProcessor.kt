package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.Entity
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.plugable.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class EntityProcessor

sealed class MatchedEntityProcessor : EntityProcessor() {
    fun process(entities: Map<String, MatcherEntityData>): Map<Attribute, Any?> {
        return when (this) {
            is MatchedEntityProcessorWithPlugin -> pluginClass.createInstance().let {
                it.setOptions(options)
                it.process(entities)
            }
            is MatchedEntityProcessorWithFunction -> function(entities)
        }
    }
}

class MatchedEntityProcessorWithPlugin(
    val pluginClass: KClass<out MatchedEntityProcessorPlugin>,
    val options: NewPlugin.Options = NewPlugin.noOptions
) : MatchedEntityProcessor()

class MatchedEntityProcessorWithFunction(
    val function: MatchedEntityProcessorFunction
) : MatchedEntityProcessor()

sealed class UnMatchedEntityProcessor(
    val entityType: Entity
) : EntityProcessor() {
    fun process(entity: PersistentSourceEntity): Map<Attribute, Any?> {
        return when (this) {
            is UnMatchedEntityProcessorWithPlugin -> pluginClass.createInstance().let {
                it.setOptions(options)
                it.process(entity)
            }
            is UnMatchedEntityProcessorWithFunction -> function(entity)
        }
    }
}

class UnMatchedEntityProcessorWithPlugin(
    entityType: Entity,
    val pluginClass: KClass<out UnMatchedEntityProcessorPlugin>,
    val options: NewPlugin.Options = NewPlugin.noOptions
) : UnMatchedEntityProcessor(entityType)

class UnMatchedEntityProcessorWithFunction(
    entityType: Entity,
    val function: UnMatchedEntityProcessorFunction
) : UnMatchedEntityProcessor(entityType)
