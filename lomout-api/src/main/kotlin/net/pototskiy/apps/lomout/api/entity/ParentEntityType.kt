package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppEntityTypeException
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl

/**
 * Entity type super type
 *
 * @constructor
 */
class ParentEntityType(
    /**
     * Parent (super) entity type
     */
    val parent: EntityType,
    /**
     * Attribute collection to exclude
     */
    val include: AttributeCollection? = null,
    /**
     * Attribute collection to include
     */
    val exclude: AttributeCollection? = null
) {
    /**
     * Parent entity type builder class
     *
     * @property helper ConfigBuildHelper The config builder helper
     * @property parent EntityType The parent entity
     * @property includes MutableList<Attribute<*>> The list of attributes to include
     * @property excludes MutableList<Attribute<*>> The list of attributes to include
     * @constructor
     */
    @ConfigDsl
    class Builder(
        private val helper: ConfigBuildHelper,
        private val parent: EntityType
    ) {
        private val includes = mutableListOf<Attribute<*>>()
        private val excludes = mutableListOf<Attribute<*>>()

        /**
         * Define attributes that should be included from parent (super) entity type, *optional*
         *
         * ```
         * ...
         *  include("attr_name", "attr_name" ...)
         * ...
         * ```
         * * attr_name: String - attribute name to include from the parent, **at least one must be defined**
         *
         * @param name Array<out String>
         */
        @PublicApi
        fun include(vararg name: String) {
            checkThatParentHasAttributes(parent, name.toList())
            name.toList().forEach {
                this.includes.add(helper.typeManager.getEntityAttribute(parent, it)!!)
            }
        }

        /**
         * Define attributes that should be excluded from parent (super) entity type, *optional*
         *
         * ```
         * ...
         *  exclude("attr_name", "attr_name" ...)
         * ...
         * ```
         * * attr_name: String - attribute name to include from the parent, **at least one must be defined**
         *
         * @param name Array<out String>
         */
        @PublicApi
        fun exclude(vararg name: String) {
            checkThatParentHasAttributes(parent, name.toList())
            name.toList().forEach {
                this.excludes.add(helper.typeManager.getEntityAttribute(parent, it)!!)
            }
        }

        /**
         * Build parent entity type
         *
         * @return ParentEntityType
         */
        fun build(): ParentEntityType = ParentEntityType(
            parent,
            if (this.includes.isEmpty()) null else AttributeCollection(this.includes),
            if (this.excludes.isEmpty()) null else AttributeCollection(this.excludes)
        )

        private fun checkThatParentHasAttributes(parent: EntityType, names: List<String>) {
            val notFound = names.minus(parent.attributes.map { it.name })
            if (notFound.isNotEmpty()) {
                throw AppEntityTypeException(
                    "Entity type<${parent.name}> has no attribute<${notFound.joinToString(",")}>"
                )
            }
        }
    }
}
