package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformFunction
import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformPlugin
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class NewTransformer<T : Any?, R : Any?> {
    fun transform(value: T): R {
        return when (this) {
            is NewTransformerWithPlugin -> pluginClass.createInstance().let {
                it.setOptions(options)
                it.transform(value)
            }
            is NewTransformerWithFunction -> function(value)
        }
    }
}

class NewTransformerWithPlugin<T : Any?, R : Any?>(
    val pluginClass: KClass<out NewValueTransformPlugin<T, R>>,
    val options: NewPlugin.Options = NewPlugin.noOptions
) : NewTransformer<T, R>()

class NewTransformerWithFunction<T : Any?, R : Any?>(
    val function: NewValueTransformFunction<T, R>
) : NewTransformer<T, R>()

