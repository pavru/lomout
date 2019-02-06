package net.pototskiy.apps.magemediation.api.plugable

class ValueTransformPlugin<T : Any, R : Any?>(
    declaredParameters: List<Parameter<*>>,
    block: ValueTransformPlugin<T, R>.() -> R
) : Plugin<ValueTransformPlugin<T, R>, R>(
    declaredParameters.plus(Parameter<T>(valueParamName)),
    block
) {
    val valueParameter = Parameter<T>(valueParamName)
    var value: T? = null

    fun transform(value: T?): R {
        if (value != null) setArgument(valueParameter, value)
        this.value = value
        return execute()
    }

    class Builder<T : Any, R : Any?> : Plugin.Builder<ValueTransformPlugin<T, R>, R>() {
        override fun build(): ValueTransformPlugin<T, R> {
            return ValueTransformPlugin(
                parameters,
                body ?: throw PluginException("Plugin type<${ValueTransformPlugin::class.simpleName}> has no body")
            )
        }
    }

    companion object {
        const val valueParamName = "value"
    }
}

typealias ValueTransformFunction<T, R> = (T) -> R

@Suppress("unused")
fun <T : Any, R : Any?> valueTransformPlugin(block: ValueTransformPlugin.Builder<T, R>.() -> Unit): ValueTransformPlugin<T, R> {
    return ValueTransformPlugin.Builder<T, R>().apply(block).build()
}

