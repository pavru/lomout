package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.NamedObject
import net.pototskiy.apps.magemediation.api.database.DatabaseException

abstract class EntityType(
    override val name: String,
    val supers: List<EntityTypeInheritance> = mutableListOf(),
    private val declaredAttributes: List<Attribute<*>>,
    open: Boolean
) : NamedObject {

    var open: Boolean = open
        private set
    private val refinedAttributes = mutableListOf<Attribute<*>>()

    val attributes: List<Attribute<*>>
        get() {
            val inherited = supers.map { inheritance ->
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
                "Attribute<${attribute.name.fullName}> can not be added, entity type<$name> is final(close)"
            )
        }
        refinedAttributes.add(attribute)
    }

    fun getAttributeOrNull(name: String): Attribute<*>? {
        val attr = EntityAttributeManager.getAttributeOrNull(AttributeName(this.name, name))
            ?: return null
        return if (attr in attributes) attr else null
    }

    fun getAttribute(name: String): Attribute<*> {
        val attr = EntityAttributeManager.getAttributeOrNull(AttributeName(this.name, name))
            ?: throw DatabaseException("Attribute<$name> is not defined in entity<$this.name>")
        return if (attr in attributes) {
            attr
        } else {
            throw DatabaseException("Attribute<$name> is not defined in entity<$this.name>")
        }
    }

    fun checkAttributeDefined(attribute: Attribute<*>) {
        if (!isAttributeDefined(attribute)) {
            throw DatabaseException("Attribute<${attribute.name}> is not defined for entity<$this.name>")
        }
    }

    @PublicApi
    fun isAttributeDefined(attribute: Attribute<*>) = attributes.any { it.name == attribute.name }

    @ConfigDsl
    class Builder(val entityType: String, private val open: Boolean) {
        val attributes = mutableListOf<Attribute<*>>()
        private val inheritances = mutableListOf<EntityTypeInheritance>()

        inline fun <reified T : Type> attribute(name: String, block: Attribute.Builder<T>.() -> Unit = {}) =
            attributes.add(Attribute.Builder<T>(entityType, name, T::class).apply(block).build())

        fun inheritFrom(name: String, block: EntityTypeInheritance.Builder.() -> Unit = {}) {
            val eType = EntityTypeManager.getEntityType(name)
                ?: throw ConfigException("Entity type<$name> does not defined")
            inheritances.add(EntityTypeInheritance.Builder(eType).apply(block).build())
        }

        fun build(): EntityType {
            return EntityTypeManager.createEntityType(entityType, inheritances, AttributeCollection(attributes), open)
        }
    }

    companion object
}
