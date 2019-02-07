package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.database.PersistentEntity
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuildFunction
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuildPlugin
import net.pototskiy.apps.magemediation.api.plugable.Parameter

sealed class AttributeBuilder<in E : PersistentEntity<*>, R : Any?> {
    fun build(entity: E): R {
        return when (this) {
            is AttributeBuilderPlugin -> {
                args.forEach { param, value ->
                    @Suppress("UNCHECKED_CAST")
                    plugin.setArgument(param as Parameter<Any>, value)
                }
                plugin.build(entity)
            }
            is AttributeBuilderFunction -> function(entity)
        }
    }
}

class AttributeBuilderPlugin<E : PersistentEntity<*>, R : Any?>(
    val plugin: AttributeBuildPlugin<E, R>,
    val args: Map<Parameter<*>, Any>
) : AttributeBuilder<E, R>()

class AttributeBuilderFunction<in E : PersistentEntity<*>, R : Any?>(
    val function: AttributeBuildFunction<E, R>
) : AttributeBuilder<E, R>()
