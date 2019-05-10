package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Base class for any attribute writer plugins
 *
 * @param T : Type The attribute value to write to cell
 */
abstract class AttributeWriterPlugin<T : Type> : Plugin() {
    /**
     * Writer function
     *
     * @param value T? The value to write
     * @param cell Cell The cell to write value
     */
    abstract fun write(value: T?, cell: Cell)
}

/**
 * Function type for inline attribute writer
 */
typealias AttributeWriterFunction<T> = PluginContextInterface.(T?, cell: Cell) -> Unit
