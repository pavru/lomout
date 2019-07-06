package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.document.Document

/**
 * Base class for any attribute builder plugins
 *
 * @param R The type builder return
 */
abstract class AttributeBuilder<R : Any?> : Plugin() {
    /**
     * Builder function
     *
     * @param entity DbEntity The entity to build value
     * @return R? The value type to return
     */
    abstract fun build(entity: Document): R
}
