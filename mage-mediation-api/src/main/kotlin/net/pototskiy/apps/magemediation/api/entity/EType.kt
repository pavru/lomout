package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.NamedObject
import net.pototskiy.apps.magemediation.api.database.DatabaseException

abstract class EType(
    val type: String,
    val inheritances: List<ETypeInheritance> = mutableListOf(),
    private val declaredAttributes: List<Attribute<*>>,
    val open: Boolean
) : NamedObject {
    override val name: String = type

    private val refinedAttributes = mutableListOf<Attribute<*>>()

    val attributes: List<Attribute<*>>
        get() {
            val inherited = inheritances.map { inheritance ->
                inheritance.parent.attributes.filter { attr ->
                    inheritance.include?.let { attr in it } ?: true
                }.filterNot { attr ->
                    inheritance.exclude?.let { attr in it } ?: false
                }
            }.flatten()
            return refinedAttributes
                .minus(inherited).plus(inherited)
                .minus(declaredAttributes).plus(declaredAttributes)
        }

    fun addAttribute(attribute: Attribute<*>) {
        if (!open) {
            throw DatabaseException(
                "Attribute<${attribute.name.fullName}> can not be added, entity type<$type> is final(close)"
            )
        }
        refinedAttributes.add(attribute)
    }

    fun getAttributeOrNull(name: String): Attribute<*>? {
        val attr = EntityAttributeManager.getAttributeOrNull(AttributeName(type, name))
            ?: return null
        return if (attr in attributes) attr else null
    }

    fun getAttribute(name: String): Attribute<*> {
        val attr = EntityAttributeManager.getAttributeOrNull(AttributeName(type, name))
            ?: throw DatabaseException("Attribute<$name> is not defined in entity<$type>")
        return if (attr in attributes) {
            attr
        } else {
            throw DatabaseException("Attribute<$name> is not defined in entity<$type>")
        }
    }

    fun checkAttributeDefined(attribute: Attribute<*>) {
        if (!isAttributeDefined(attribute)) {
            throw DatabaseException("Attribute<${attribute.name}> is not defined for entity<$type>")
        }
    }

    @PublicApi
    fun isAttributeDefined(attribute: Attribute<*>) = attributes.any { it.name == attribute.name }

    @ConfigDsl
    class Builder(@property:ConfigDsl val entityType: String, private val open: Boolean) {
        @ConfigDsl val attributes = mutableListOf<Attribute<*>>()
        private val inheritances = mutableListOf<ETypeInheritance>()

        inline fun <reified T : Type> attribute(name: String, block: Attribute.Builder<T>.() -> Unit = {}) =
            attributes.add(Attribute.Builder<T>(entityType, name, T::class).apply(block).build())

        fun inheritFrom(name: String, block: ETypeInheritance.Builder.() -> Unit = {}) {
            val eType = EntityTypeManager.getEntityType(name)
                ?: throw ConfigException("Entity type<$name> does not defined")
            inheritances.add(ETypeInheritance.Builder(eType).apply(block).build())
        }

        fun build(): EType {
            return EntityTypeManager.createEntityType(entityType, inheritances, AttributeCollection(attributes), open)
        }
    }

    companion object
}
