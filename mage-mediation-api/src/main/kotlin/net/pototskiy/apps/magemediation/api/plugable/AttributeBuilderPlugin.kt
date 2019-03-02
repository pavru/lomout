package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.entity.Type

abstract class AttributeBuilderPlugin<R : Type> : Plugin() {
    abstract fun build(entity: DbEntity): R?
}

typealias AttributeBuilderFunction<R> = (DbEntity) -> R?
