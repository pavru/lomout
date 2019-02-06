package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.Config

abstract class Plugin<T : Plugin<T, R>, R>(
    private val declaredParameters: List<Parameter<*>>,
    protected val body: T.() -> R
) {
    private val arguments: MutableMap<Parameter<*>, Any?> = mutableMapOf()

    fun <P: Any> setArgument(parameter: Parameter<P>, value: P) {
        checkParameterDeclared(parameter)
        arguments[parameter] = value
    }

    fun <P : Any> getArgument(parameter: Parameter<P>): P? = getArgumentNoDefault(parameter) ?: parameter.default

    @Suppress("MemberVisibilityCanBePrivate")
    fun <P : Any> getArgumentNoDefault(parameter: Parameter<P>): P? {
        checkParameterDeclared(parameter)
        @Suppress("UNCHECKED_CAST")
        val value = arguments[parameter] as? P
        if (value == null && arguments[parameter] != null) {
            throw PluginException("Wrong argument type for parameter<${parameter.name}>")
        }
        @Suppress("UNCHECKED_CAST")
        return value
    }

    private fun <P:Any> checkParameterDeclared(parameter: Parameter<P>) {
        declaredParameters.find { it == parameter }
            ?: throw PluginException("Plugin parameter<${parameter.name}> is not declared for plugin<${this::class.simpleName}>")
    }

    @Suppress("UNCHECKED_CAST")
    fun execute(): R = (this as T).body()

    companion object {
        lateinit var config: Config
    }

    abstract class Builder<P : Plugin<P, R>, R> {
        protected val parameters = mutableListOf<Parameter<*>>()
        protected var body: (P.() -> R)? = null

        @Suppress("unused")
        fun <T : Any> Builder<P, R>.parameter(name: String, default: T? = null) {
            parameters.add(Parameter(name, default))
        }

        @Suppress("unused")
        fun Builder<P, R>.execute(block: P.() -> R) {
            this.body = block
        }

        abstract fun build(): P
    }
}
