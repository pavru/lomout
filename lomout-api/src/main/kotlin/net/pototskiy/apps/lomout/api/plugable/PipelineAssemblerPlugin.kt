package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.entity.EntityCollection
import kotlin.reflect.KClass

/**
 * Base class for any pipeline assemblers
 */
abstract class PipelineAssemblerPlugin : Plugin() {
    /**
     * Assembler function
     *
     * @param target The target entity type
     * @param entities The pipeline entity collection
     * @return The attribute→value map for target entity
     */
    abstract fun assemble(target: KClass<out Document>, entities: EntityCollection): Map<Attribute, Any>
}

/**
 * Function type for inline pipeline assembler
 */
typealias PipelineAssemblerFunction =
        PluginContextInterface.(target: KClass<out Document>, entities: EntityCollection) -> Map<Attribute, Any>
