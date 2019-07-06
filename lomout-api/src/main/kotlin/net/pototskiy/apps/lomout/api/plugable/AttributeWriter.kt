package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.source.workbook.Cell
import kotlin.reflect.full.createInstance

/**
 * Base class for any attribute writer plugins
 *
 * @param T The attribute value to write to cell
 */
abstract class AttributeWriter<T : Any?> : Plugin() {
    /**
     * Writer function
     *
     * @param value T? The value to write
     * @param cell Cell The cell to write value
     */
    abstract fun write(value: T, cell: Cell)
}

/**
 * Create attribute writer and apply parameters
 *
 * @param parameter Parameters set block
 * @return W
 */
inline fun <reified W : AttributeWriter<*>> createWriter(parameter: W.() -> Unit = {}): W {
    return W::class.createInstance().apply(parameter)
}
