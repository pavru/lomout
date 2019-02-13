package net.pototskiy.apps.magemediation.api.plugable

class ValueTransformPlugin<T : Any, R : Any?>(
    declaredParameters: List<Parameter<*>>,
    block: ValueTransformPlugin<T, R>.() -> R
) : Plugin<ValueTransformPlugin<T, R>, R>(
    declaredParameters.plus(Parameter<T>(valueParamName)),
    block
) {
    var value: T? = null

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

@Suppress("unused")
fun <T : Any, R : Any?> valueTransformPlugin(block: ValueTransformPlugin.Builder<T, R>.() -> Unit): ValueTransformPlugin<T, R> {
    return ValueTransformPlugin.Builder<T, R>().apply(block).build()
}

