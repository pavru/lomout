package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.database.source.PersistentEntity


class AttributeBuildPlugin<E : PersistentEntity<*>, R : Any?>(
    declaredParameters: List<Parameter<*>>,
    block: AttributeBuildPlugin<E, R>.() -> R
) : Plugin<AttributeBuildPlugin<E, R>, R>(
    declaredParameters.plus(Parameter<E>(entityParamName)),
    block
) {
    var entityParameter = Parameter<E>(entityParamName)
        private set
    lateinit var entity: E

    fun build(entity: E): R {
        setArgument(entityParameter, entity)
        this.entity = entity
        return execute()
    }

    class Builder<E : PersistentEntity<*>, R : Any?> : Plugin.Builder<AttributeBuildPlugin<E, R>, R>() {
        override fun build(): AttributeBuildPlugin<E, R> {
            return AttributeBuildPlugin(
                parameters,
                body ?: throw PluginException("Plugin<${AttributeBuildPlugin::class.simpleName} has no body>")
            )
        }
    }

    companion object {
        const val entityParamName = "entity"
    }
}

typealias AttributeBuildFunction<E, R> = (E) -> R

@Suppress("unused")
fun <E : PersistentEntity<*>, R : Any?> attributeBuilderPlugin(block: AttributeBuildPlugin.Builder<E, R>.() -> Unit): AttributeBuildPlugin<E, R> {
    return AttributeBuildPlugin.Builder<E, R>().apply(block).build()
}

