package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.Generated
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.document.Document
import kotlin.reflect.KClass

/**
 * Pipeline input entity
 *
 * @property entity EntityType The entity type
 * @property includeDeleted The flag to include deleted entities
 * @constructor
 */
data class InputEntity(
    val entity: KClass<out Document>,
    val includeDeleted: Boolean
) {

    /**
     * Pipeline input entity definition builder class
     *
     * @property helper The config build helper
     * @property entityType The base entity type
     * @property includeDeleted The flag to include deleted entity to pipeline
     * @constructor
     */
    @ConfigDsl
    class Builder(
        val helper: ConfigBuildHelper,
        val entityType: KClass<out Document>
    ) {
        private var includeDeleted: Boolean = false

        /**
         * Indicate that deleted entities must be included in a pipeline.
         */
        @ConfigDsl
        fun includeDeleted() {
            this.includeDeleted = true
        }

        /**
         * Build input entity definition
         *
         * @return InputEntity
         */
        fun build(): InputEntity = InputEntity(entityType, includeDeleted)
    }

    /**
     * Test equal
     *
     * @param other The other
     * @return The result
     */
    @Generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InputEntity) return false

        if (entity != other.entity) return false
        if (includeDeleted != other.includeDeleted) return false

        return true
    }

    /**
     * Generate has code
     *
     * @return Int
     */
    override fun hashCode(): Int {
        var result = entity.hashCode()
        result = 31 * result + includeDeleted.hashCode()
        return result
    }
}
