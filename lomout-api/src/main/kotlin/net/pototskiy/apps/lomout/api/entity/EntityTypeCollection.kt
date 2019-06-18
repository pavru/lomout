package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl

/**
 * Entity collection configuration
 *
 * @property value List<EntityType>
 * @constructor
 */
class EntityTypeCollection(private val value: List<EntityType>) : List<EntityType> by value {
    /**
     * Entity type collection builder
     *
     * @property helper ConfigBuildHelper
     * @property eTypes MutableList<EntityType>
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private val eTypes = mutableListOf<EntityType>()

        /**
         * Entity type definition
         *
         * ```
         * ...
         *  entity("name", isOpen:Boolean) {
         *      inheritFrom("entity type") {...}
         *      inheritFrom("entity type") {...}
         *      ...
         *      attribute<Type>("name") {...}
         *      attribute<Type>("name") {...}
         *      ...
         *  }
         * ...
         * ```
         * * name — entity type name, must be unique, **mandatory**
         * * isOpen — true — it's allowed to add the attribute from source file automatically,
         *      false — allow only declared attributes for entity type, optional
         * * [inheritFrom][EntityType.Builder.inheritFrom] — inherit attributes from another entity type, optional
         * * [attribute][EntityType.Builder.attribute] — entity type attribute definition,
         *  **at least one must be defined**
         *
         * @see EntityType
         * @see EntityType.Builder
         *
         * @param name Entity type name
         * @param open Entity allows adding attributes after creation
         * @param block The entity configuration
         * @return Boolean
         */
        fun entity(name: String, open: Boolean, block: EntityType.Builder.() -> Unit) =
            eTypes.add(EntityType.Builder(helper, name, open).apply(block).build())

        /**
         * Build entity type collection
         *
         * @return EntityTypeCollection
         */
        fun build(): EntityTypeCollection =
            EntityTypeCollection(eTypes)
    }
}
