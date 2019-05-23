package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.plugable.AttributeReaderFunction
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Abstract attribute reader
 *
 * @param T The reader return type
 */
sealed class AttributeReader<T : Type> {
    /**
     * Reader function
     *
     * @param attribute The destination attribute
     * @param input Cell The cell to read value
     * @return T?
     */
    fun read(attribute: Attribute<out T>, input: Cell): T? {
        return when (this) {
            is AttributeReaderWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.read(attribute, input)
            }
            is AttributeReaderWithFunction -> PluginContext.function(attribute, input)
        }
    }
}

/**
 * Attribute reader with a plugin
 *
 * @param T The reader return type
 * @constructor
 */
open class AttributeReaderWithPlugin<T : Type>(
    /**
     * Plugin class
     */
    val pluginClass: KClass<out AttributeReaderPlugin<T>>,
    /**
     * Plugin options
     */
    val options: AttributeReaderPlugin<T>.() -> Unit = {}
) : AttributeReader<T>()

/**
 * Attribute reader with function
 *
 * @param T The reader return type
 * @constructor
 */
open class AttributeReaderWithFunction<T : Type>(
    /**
     * Writer function
     */
    val function: AttributeReaderFunction<T>
) : AttributeReader<T>()
