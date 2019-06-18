package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.plugable.AttributeBuilderFunction
import net.pototskiy.apps.lomout.api.plugable.AttributeBuilderPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Abstract attribute builder
 *
 * @param R The type builder return
 */
sealed class AttributeBuilder<R : Type> {
    /**
     * Builder function
     *
     * @param entity DbEntity The DB entity
     * @return R?
     */
    operator fun invoke(entity: Entity): R? {
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
 * Attribute builder with a plugin
 *
 * @param R The builder return type
 * @property pluginClass The plugin class
 * @property options The plugin options
 * @constructor
 */
class AttributeBuilderWithPlugin<R : Type>(
    val pluginClass: KClass<out AttributeBuilderPlugin<R>>,
    val options: AttributeBuilderPlugin<R>.() -> Unit = {}
) : AttributeBuilder<R>()

/**
 * Attribute builder with function
 *
 * @param R The builder return type
 * @property function The builder function
 * @constructor
 */
class AttributeBuilderWithFunction<R : Type>(
    val function: AttributeBuilderFunction<R>
) : AttributeBuilder<R>()
