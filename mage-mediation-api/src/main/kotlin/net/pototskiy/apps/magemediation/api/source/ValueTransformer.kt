package net.pototskiy.apps.magemediation.api.source

import net.pototskiy.apps.magemediation.api.plugable.ValueTransformFunction
import net.pototskiy.apps.magemediation.api.plugable.ValueTransformPlugin
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class ValueTransformer<T : Any?, R : Any?> {
    fun transform(value: T): R {
        return when (this) {
            is ValueTransformerWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.transform(value)
            }
            is ValueTransformerWithFunction -> function(value)
        }
    }
}

class ValueTransformerWithPlugin<T : Any?, R : Any?>(
    val pluginClass: KClass<out ValueTransformPlugin<T, R>>,
    val options: ValueTransformPlugin<T, R>.() -> Unit
) : ValueTransformer<T, R>()

class ValueTransformerWithFunction<T : Any?, R : Any?>(
    val function: ValueTransformFunction<T, R>
) : ValueTransformer<T, R>()
