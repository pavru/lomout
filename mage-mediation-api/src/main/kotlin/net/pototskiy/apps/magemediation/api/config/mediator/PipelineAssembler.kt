package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.Entity
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import net.pototskiy.apps.magemediation.api.plugable.PipelineAssemblerFunction
import net.pototskiy.apps.magemediation.api.plugable.PipelineAssemblerPlugin
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class PipelineAssembler {
    fun assemble(target: Entity, entities: PipelineDataCollection): Map<Attribute, Any?> {
        return when (this) {
            is PipelineAssemblerWithPlugin -> pluginClass.createInstance().let {
                it.setOptions(options)
                it.assemble(target, entities)
            }
            is PipelineAssemblerWithFunction -> function(target, entities)
        }
    }
}

class PipelineAssemblerWithPlugin(
    val pluginClass: KClass<out PipelineAssemblerPlugin>,
    val options: NewPlugin.Options = NewPlugin.noOptions
) : PipelineAssembler()

class PipelineAssemblerWithFunction(
    val function: PipelineAssemblerFunction
) : PipelineAssembler()
