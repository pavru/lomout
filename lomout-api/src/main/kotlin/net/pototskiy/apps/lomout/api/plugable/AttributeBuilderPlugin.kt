package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.entity.Type

/**
 * Base class for any attribute builder plugins
 *
 * @param R : Type The type builder return
 */
abstract class AttributeBuilderPlugin<R : Type> : Plugin() {
    /**
     * Builder function
     *
     * @param entity DbEntity The entity to build value
     * @return R? The value type to return
     */
    abstract fun build(entity: DbEntity): R?
}

/**
 * Function type for inline builder
 */
typealias AttributeBuilderFunction<R> = PluginContextInterface.(DbEntity) -> R?
