package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.plugable.AttributeBuilderFunction
import net.pototskiy.apps.lomout.api.plugable.AttributeBuilderPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Abstract attribute builder
 *
 * @param R : Type The type builder return
 */
sealed class AttributeBuilder<R : Type> {
    /**
     * Builder function
     *
     * @param entity DbEntity The DB entity
     * @return R?
     */
    fun build(entity: DbEntity): R? {
        return when (this) {
            is AttributeBuilderWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.build(entity)
            }
            is AttributeBuilderWithFunction -> PluginContext.function(entity)
        }
    }
}

/**
 * Attribute builder with plugin
 *
 * @param R : Type The builder return type
 * @property pluginClass KClass<out AttributeBuilderPlugin<R>> The plugin class
 * @property options [AttributeBuilderPlugin<R>.()] Function1<AttributeBuilderPlugin<R>, Unit> The plugin options
 * @constructor
 */
class AttributeBuilderWithPlugin<R : Type>(
    val pluginClass: KClass<out AttributeBuilderPlugin<R>>,
    val options: AttributeBuilderPlugin<R>.() -> Unit = {}
) : AttributeBuilder<R>()

/**
 * Attribute builder with function
 *
 * @param R : Type The builder return type
 * @property function [AttributeBuilderFunction] Function2<PluginContextInterface, DbEntity, R?> The builder function
 * @constructor
 */
class AttributeBuilderWithFunction<R : Type>(
    val function: AttributeBuilderFunction<R>
) : AttributeBuilder<R>()
