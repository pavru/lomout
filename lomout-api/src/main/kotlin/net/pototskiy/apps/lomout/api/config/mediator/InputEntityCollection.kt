package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.unknownPlace
import kotlin.reflect.KClass

/**
 * Pipeline input entities configuration
 *
 * @property entities List<InputEntity>
 * @constructor
 */
data class InputEntityCollection(private val entities: List<InputEntity>) : List<InputEntity> by entities {
    /**
     * Input entities builder class
     *
     * @property helper ConfigBuildHelper The config build helper
     * @property entities MutableList<InputEntity> Input entities
     * @constructor
     */
    @ConfigDsl
    class Builder(val helper: ConfigBuildHelper) {
        private val entities = mutableListOf<InputEntity>()

        /**
         * Define input entity as reference to already defined one
         *
         * ```
         * ...
         *  entity(entityClass) {
         *      includeDeleted()
         *  }
         * ...
         * ```
         * * entityClass — The entity type class
         * * includeDeleted() — Set the flag to include deleted entities
         *
         * @param entityType The entity type class
         * @param block The entity definition
         */
        @ConfigDsl
        fun entity(entityType: KClass<out Document>, block: InputEntity.Builder.() -> Unit = {}) {
            entities.add(
                InputEntity
                    .Builder(helper, entityType)
                    .apply(block)
                    .build()
            )
        }

        /**
         * Build input entities collection
         *
         * @return InputEntityCollection
         */
        fun build(): InputEntityCollection {
            if (entities.isEmpty()) {
                throw AppConfigException(unknownPlace(), "At least one input entity must be defined.")
            }
            return InputEntityCollection(entities)
        }
    }
}
