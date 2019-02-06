package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.plugable.Parameter
import net.pototskiy.apps.magemediation.api.plugable.ValueTransformFunction
import net.pototskiy.apps.magemediation.api.plugable.ValueTransformPlugin

sealed class Transformer<in T : Any?, R : Any?> {
    fun transform(value: T): R {
        return when (this) {
            is TransformerPlugin -> {
                args.forEach { param, arg ->
                    @Suppress("UNCHECKED_CAST")
                    plugin.setArgument(param as Parameter<Any>, arg)
                }
                plugin.transform(value)
            }
            is TransformerFunction -> function(value)
        }
    }
}

class TransformerPlugin<T : Any, R : Any?>(
    val plugin: ValueTransformPlugin<T, R>,
    val args: Map<Parameter<*>, Any> = emptyMap()
) : Transformer<T, R>()

class TransformerFunction<in T : Any?, R : Any?>(val function: ValueTransformFunction<T, R>) : Transformer<T, R>()
