package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.entity.Type

abstract class AttributeBuilderPlugin<R : Type> : Plugin() {
    abstract fun build(entity: DbEntity): R?
}

typealias AttributeBuilderFunction<R> = PluginContextInterface.(DbEntity) -> R?
