package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException

class EntityTypeInheritance(
    val parent: EntityType,
    val include: AttributeCollection? = null,
    val exclude: AttributeCollection? = null
) {
    private fun findAttribute(name: String): Attribute<*>? {
        val attr = EntityAttributeManager.getAttributeOrNull(AttributeName(parent.name, name))
        return if (attr != null) {
            when {
                include?.contains(attr) == true -> attr
                exclude?.contains(attr) == true -> null
                else -> attr
            }
        } else {
            null
        }
    }

    fun findAttributeRecursive(name: String): Attribute<*>? {
        val attr = findAttribute(name)
        return if (attr != null) {
            attr
        } else {
            var iAttr: Attribute<*>? = null
            for (inheritance in parent.supers) {
                iAttr = inheritance.findAttributeRecursive(name)
                if (iAttr != null) break
            }
            iAttr
        }
    }

    @Suppress("TooManyFunctions")
    @ConfigDsl
    class Builder(private val parent: EntityType) {
        private val includes = mutableListOf<Attribute<*>>()
        private val excludes = mutableListOf<Attribute<*>>()

        @PublicApi
        fun include(vararg name: String) {
            checkThatParentHasAttributes(parent, name.toList())
            name.toList().forEach {
                this.includes.add(EntityAttributeManager.getAttributeOrNull(AttributeName(parent.name, it))!!)
            }
        }

        @PublicApi
        fun exclude(vararg name: String) {
            checkThatParentHasAttributes(parent, name.toList())
            name.toList().forEach {
                this.excludes.add(EntityAttributeManager.getAttributeOrNull(AttributeName(parent.name, it))!!)
            }
        }

        fun build(): EntityTypeInheritance = EntityTypeInheritance(
            parent,
            if (this.includes.isEmpty()) null else AttributeCollection(this.includes),
            if (this.excludes.isEmpty()) null else AttributeCollection(this.excludes)
        )

        private fun checkThatParentHasAttributes(parent: EntityType, names: List<String>) {
            val notFound = names.minus(parent.attributes.map { it.name.attributeName })
            if (notFound.isNotEmpty()) {
                throw ConfigException("Entity type<${parent.name}> has no attribute<${notFound.joinToString(",")}>")
            }
        }
    }
}
