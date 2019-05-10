package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Base class for any attribute reader plugins
 *
 * @param T : Type The value type to return
 */
abstract class AttributeReaderPlugin<T : Type> : Plugin() {
    /**
     * Reader function
     *
     * @param attribute Attribute<out T> The attribute to read
     * @param input Cell The cell to read attribute value
     * @return T? The read value
     */
    abstract fun read(attribute: Attribute<out T>, input: Cell): T?
}

/**
 * Function type for inline attribute reader
 */
typealias AttributeReaderFunction<T> = PluginContextInterface.(Attribute<out T>, Cell) -> T?
