package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterFunction
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class AttributeWriter<T : Type> {
    fun write(attribute: Attribute<T>, value: T?, cell: Cell) {
        return when (this) {
            is AttributeWriterWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.write(attribute, value, cell)
            }
            is AttributeWriterWithFunction -> function(attribute, value, cell)
        }
    }
}

open class AttributeWriterWithPlugin<T : Type>(
    val pluginClass: KClass<out AttributeWriterPlugin<T>>,
    val options: AttributeWriterPlugin<T>.()->Unit = {}
) : AttributeWriter<T>()

open class AttributeWriterWithFunction<T : Type>(
    val function: AttributeWriterFunction<T>
) : AttributeWriter<T>()

