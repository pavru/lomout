package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.database.PersistentEntity
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuilderFunction
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuilderPlugin
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class AttributeBuilder<in E : PersistentEntity<*>, R : Any?> {
    fun build(entity: E): R {
        return when (this) {
            is AttributeBuilderWithPlugin -> pluginClass.createInstance().let {
                it.setOptions(options)
                it.build(entity)
            }
            is AttributeBuilderWithFunction -> function(entity)
        }
    }
}

class AttributeBuilderWithPlugin<E : PersistentEntity<*>, R : Any?>(
    val pluginClass: KClass<out AttributeBuilderPlugin<E, R>>,
    val options: NewPlugin.Options = NewPlugin.noOptions
) : AttributeBuilder<E, R>()

class AttributeBuilderWithFunction<in E : PersistentEntity<*>, R : Any?>(
    val function: AttributeBuilderFunction<E, R>
) : AttributeBuilder<E, R>()
