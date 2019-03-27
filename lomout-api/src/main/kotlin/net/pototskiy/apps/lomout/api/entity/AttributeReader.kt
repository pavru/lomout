package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.plugable.AttributeReaderFunction
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class AttributeReader<T : Type> {
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

open class AttributeReaderWithPlugin<T : Type>(
    val pluginClass: KClass<out AttributeReaderPlugin<T>>,
    val options: AttributeReaderPlugin<T>.() -> Unit = {}
) : AttributeReader<T>()

open class AttributeReaderWithFunction<T : Type>(
    val function: AttributeReaderFunction<T>
) : AttributeReader<T>()
