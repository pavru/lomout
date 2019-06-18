package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.entity.Entity
import net.pototskiy.apps.lomout.api.entity.type.Type

/**
 * Base class for any attribute builder plugins
 *
 * @param R The type builder return
 */
abstract class AttributeBuilderPlugin<R : Type> : Plugin() {
    /**
     * Builder function
     *
     * @param entity DbEntity The entity to build value
     * @return R? The value type to return
     */
    abstract fun build(entity: Entity): R?
}

/**
 * Function type for inline builder
 */
typealias AttributeBuilderFunction<R> = PluginContextInterface.(Entity) -> R?
