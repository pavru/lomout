package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException

class ETypeInheritance(
    val parent: EType,
    val include: AttributeCollection? = null,
    val exclude: AttributeCollection? = null
) {
    private fun findAttribute(name: String): Attribute<*>? {
        val attr = EntityAttributeManager.getAttributeOrNull(AttributeName(parent.type, name))
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
            for (inheritance in parent.inheritances) {
                iAttr = inheritance.findAttributeRecursive(name)
                if (iAttr != null) break
            }
            iAttr
        }
    }

    @Suppress("TooManyFunctions")
    @ConfigDsl
    class Builder(private val parent: EType) {
        private val includes = mutableListOf<Attribute<*>>()
        private val excludes = mutableListOf<Attribute<*>>()

        @PublicApi
        fun include(vararg name: String) {
            checkThatParentHasAttributes(parent, name.toList())
            name.toList().forEach {
                this.includes.add(EntityAttributeManager.getAttributeOrNull(AttributeName(parent.type, it))!!)
            }
        }

        @PublicApi
        fun exclude(vararg name: String) {
            checkThatParentHasAttributes(parent, name.toList())
            name.toList().forEach {
                this.excludes.add(EntityAttributeManager.getAttributeOrNull(AttributeName(parent.type, it))!!)
            }
        }

        fun build(): ETypeInheritance = ETypeInheritance(
            parent,
            if (this.includes.isEmpty()) null else AttributeCollection(this.includes),
            if (this.excludes.isEmpty()) null else AttributeCollection(this.excludes)
        )

        private fun checkThatParentHasAttributes(parent: EType, names: List<String>) {
            val notFound = names.minus(parent.attributes.map { it.name.attributeName })
            if (notFound.isNotEmpty()) {
                throw ConfigException("Entity type<${parent.type}> has no attribute<${notFound.joinToString(",")}>")
            }
        }
    }
}
