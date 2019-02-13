package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.database.PersistentEntity


abstract class AttributeBuilderPlugin<E : PersistentEntity<*>, R : Any?> : NewPlugin<R>() {
    protected lateinit var entity: E

    fun build(entity: E): R {
        this.entity = entity
        return execute()
    }

    open class Options: NewPlugin.Options()
}

typealias AttributeBuilderFunction<E, R> = (E) -> R

