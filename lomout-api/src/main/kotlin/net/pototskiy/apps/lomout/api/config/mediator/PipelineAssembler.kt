package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerFunction
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class PipelineAssembler {
    fun assemble(target: EntityType, entities: PipelineDataCollection): Map<AnyTypeAttribute, Type?> {
        return when (this) {
            is PipelineAssemblerWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.assemble(target, entities)
            }
            is PipelineAssemblerWithFunction -> PluginContext.function(target, entities)
        }
    }
}

class PipelineAssemblerWithPlugin(
    val pluginClass: KClass<out PipelineAssemblerPlugin>,
    val options: PipelineAssemblerPlugin.() -> Unit = {}
) : PipelineAssembler()

class PipelineAssemblerWithFunction(
    val function: PipelineAssemblerFunction
) : PipelineAssembler()
