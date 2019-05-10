package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.Type

/**
 * Base class for any pipeline assemblers
 */
abstract class PipelineAssemblerPlugin : Plugin() {
    /**
     * Assembler function
     *
     * @param target EntityType The target entity type
     * @param entities PipelineDataCollection The pipeline entity collection
     * @return Map<AnyTypeAttribute, Type?> The attribute->value map for target entity
     */
    abstract fun assemble(
        target: EntityType,
        entities: PipelineDataCollection
    ): Map<AnyTypeAttribute, Type?>
}

/**
 * Function type for inline pipeline assembler
 */
typealias PipelineAssemblerFunction =
        PluginContextInterface.(
            target: EntityType,
            entities: PipelineDataCollection
        ) -> Map<AnyTypeAttribute, Type?>
