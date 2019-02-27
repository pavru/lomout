package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException

class ETypeInheritance (
    val parent: EType,
    val include: AttributeCollection? = null,
    val exclude: AttributeCollection? = null
) {
    @ConfigDsl
    class Builder(private val parent: EType) {
        private val includes = mutableListOf<Attribute<*>>()
        private val excludes = mutableListOf<Attribute<*>>()

        fun include(vararg name: String) {
            checkThatParentHasAttributes(parent, name.toList())
            name.toList().forEach {
                this.includes.add(EntityAttributeManager.getAttributeOrNull(AttributeName(parent.type,it))!!)
            }
        }


        fun exclude(vararg name: String) {
            checkThatParentHasAttributes(parent,name.toList())
            name.toList().forEach {
                this.excludes.add(EntityAttributeManager.getAttributeOrNull(AttributeName(parent.type,it))!!)
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
