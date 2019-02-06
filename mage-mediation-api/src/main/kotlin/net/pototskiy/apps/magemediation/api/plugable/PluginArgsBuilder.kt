package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.ConfigDsl

@ConfigDsl
class PluginArgsBuilder {
    private val args = mutableMapOf<Parameter<*>, Any>()

    @Suppress("unused")
    fun <T : Any> PluginArgsBuilder.parameter(name: String) = Parameter<T>(name)

    infix fun <T : Any> Parameter<T>.to(value: T) = args.put(this, value)

    fun build(): Map<Parameter<*>, Any> = args.toMap()
}
