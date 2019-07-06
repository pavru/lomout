package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import kotlin.reflect.full.createInstance

/**
 * Base class for any attribute reader plugins
 *
 * @param T The value type to return
 */
abstract class AttributeReader<T : Any?> : Plugin() {
    /**
     * Reader function
     *
     * @param attribute Attribute<out T> The attribute to read
     * @param input Cell The cell to read attribute value
     * @return T? The read value
     */
    abstract fun read(attribute: DocumentMetadata.Attribute, input: Cell): T
}

/**
 * Create attribute reader and apply parameters
 *
 * @param parameters Parameters set block
 * @return R
 */
inline fun <reified R : AttributeReader<*>> createReader(parameters: R.() -> Unit = {}): R {
    return R::class.createInstance().apply(parameters)
}
