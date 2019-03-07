package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException

class ParentEntityType(
    val parent: EntityType,
    val include: AttributeCollection? = null,
    val exclude: AttributeCollection? = null
) {
    @ConfigDsl
    class Builder(
        private val typeManager: EntityTypeManager,
        private val parent: EntityType
    ) {
        private val includes = mutableListOf<Attribute<*>>()
        private val excludes = mutableListOf<Attribute<*>>()

        @PublicApi
        fun include(vararg name: String) {
            checkThatParentHasAttributes(parent, name.toList())
            name.toList().forEach {
                this.includes.add(typeManager.getEntityAttribute(parent, it)!!)
            }
        }

        @PublicApi
        fun exclude(vararg name: String) {
            checkThatParentHasAttributes(parent, name.toList())
            name.toList().forEach {
                this.excludes.add(typeManager.getEntityAttribute(parent, it)!!)
            }
        }

        fun build(): ParentEntityType = ParentEntityType(
            parent,
            if (this.includes.isEmpty()) null else AttributeCollection(this.includes),
            if (this.excludes.isEmpty()) null else AttributeCollection(this.excludes)
        )

        private fun checkThatParentHasAttributes(parent: EntityType, names: List<String>) {
            val notFound = names.minus(parent.attributes.map { it.name })
            if (notFound.isNotEmpty()) {
                throw ConfigException("Entity type<${parent.name}> has no attribute<${notFound.joinToString(",")}>")
            }
        }
    }
}
