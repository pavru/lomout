package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerFunction
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Abstract pipeline assembler
 */
sealed class PipelineAssembler {
    /**
     * Assembler function
     *
     * @param target EntityType The target entity type
     * @param entities PipelineDataCollection The pipeline input entities
     * @return Map<AnyTypeAttribute, Type?> The target entity attributes
     */
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

/**
 * Pipeline assembler with a plugin
 *
 * @property pluginClass KClass<out PipelineAssemblerPlugin> The assembler plugin class
 * @property options The plugin options
 * @constructor
 */
class PipelineAssemblerWithPlugin(
    val pluginClass: KClass<out PipelineAssemblerPlugin>,
    val options: PipelineAssemblerPlugin.() -> Unit = {}
) : PipelineAssembler()

/**
 * Pipeline assembler with inline function
 *
 * @property function [PipelineAssemblerFunction] The assembler function
 * @constructor
 */
class PipelineAssemblerWithFunction(
    val function: PipelineAssemblerFunction
) : PipelineAssembler()
