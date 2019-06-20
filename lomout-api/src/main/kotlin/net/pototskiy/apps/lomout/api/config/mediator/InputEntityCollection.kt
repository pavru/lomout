package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.unknownPlace

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
         *  entity("type name") {
         *      statuses(...)
         *      extAttribute("ext attribute name", "regular attribute name") {...}
         *      extAttribute("ext attribute name", "regular attribute name") {...}
         *      ...
         *  }
         * ...
         * ```
         * * filter — SQL filter for entities, usually used for filter entity by status, *optional*,
         *      **only one filter definition is allowed**
         * * statuses — [net.pototskiy.apps.lomout.api.entity.EntityStatus] — The entity status to get
         * * extAttribute(...) — define an extended attribute that is function of regular attribute, this attribute
         *      accessible only in pipelines
         *
         * @param name String
         * @param block The entity definition
         */
        @ConfigDsl
        fun entity(name: String, block: InputEntity.Builder.() -> Unit = {}) {
            val entity = helper.typeManager.getEntityType(name)
                ?: throw AppConfigException(unknownPlace(), "Entity has not been defined yet.")
            entities.add(
                InputEntity
                    .Builder(helper, entity)
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
