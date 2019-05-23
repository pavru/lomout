package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.plugable.AttributeWriterFunction
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Abstract attribute writer
 *
 * @param T The value to write type
 */
sealed class AttributeWriter<T : Type> {
    /**
     * Writer function
     *
     * @param value T? The value to write
     * @param cell Cell The cell to write
     */
    fun write(value: T?, cell: Cell) {
        return when (this) {
            is AttributeWriterWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.write(value, cell)
            }
            is AttributeWriterWithFunction -> PluginContext.function(value, cell)
        }
    }
}

/**
 * Attribute writer with a plugin
 *
 * @param T Type The value to write type
 * @property pluginClass KClass<out AttributeWriterPlugin<T>> The plugin class
 * @property options [AttributeWriterPlugin<T>.()] Function1<AttributeWriterPlugin<T>, Unit> The plugin options
 * @constructor
 */
open class AttributeWriterWithPlugin<T : Type>(
    val pluginClass: KClass<out AttributeWriterPlugin<T>>,
    val options: AttributeWriterPlugin<T>.() -> Unit = {}
) : AttributeWriter<T>()

/**
 * Attribute writer with function
 *
 * @param T The value type to write
 * @property function The writer function
 * @constructor
 */
open class AttributeWriterWithFunction<T : Type>(
    val function: AttributeWriterFunction<T>
) : AttributeWriter<T>()
