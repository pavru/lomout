package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.NamedObject
import net.pototskiy.apps.magemediation.api.database.DatabaseException

abstract class EntityType(
    override val name: String,
    val open: Boolean
) : NamedObject {

    lateinit var manager: EntityTypeManagerInterface

    val attributes: List<Attribute<*>>
        get() = manager.getEntityTypeAttributes(this)

    fun getAttributeOrNull(name: String): Attribute<*>? {
        val attr = EntityTypeManager.getEntityAttribute(this, name)
            ?: return null
        return if (attr in attributes) attr else null
    }

    fun getAttribute(name: String): Attribute<*> = manager.getEntityAttribute(this, name)
        ?: throw DatabaseException("Attribute<$name> is not defined in entity<$this.name>")

    fun checkAttributeDefined(attribute: Attribute<*>) {
        if (!isAttributeDefined(attribute)) {
            throw DatabaseException("Attribute<${attribute.name}> is not defined for entity<$this.name>")
        }
    }

    @PublicApi
    fun isAttributeDefined(attribute: Attribute<*>) = attributes.any { it.name == attribute.name }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntityType) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "EntityType(name='$name', open=$open)"
    }

    @ConfigDsl
    class Builder(val entityType: String, private val open: Boolean) {
        val attributes = mutableListOf<Attribute<*>>()
        private val inheritances = mutableListOf<ParentEntityType>()

        inline fun <reified T : Type> attribute(name: String, block: Attribute.Builder<T>.() -> Unit = {}) =
            attributes.add(Attribute.Builder<T>(name, T::class).apply(block).build())

        fun inheritFrom(name: String, block: ParentEntityType.Builder.() -> Unit = {}) {
            val eType = EntityTypeManager.getEntityType(name)
                ?: throw ConfigException("Entity type<$name> does not defined")
            inheritances.add(ParentEntityType.Builder(eType).apply(block).build())
        }

        fun build(): EntityType {
            return EntityTypeManager.createEntityType(entityType, inheritances, open).also {
                EntityTypeManager.initialAttributeSetup(it, AttributeCollection(attributes))
            }
        }
    }
}

operator fun EntityType.get(attribute: String) = this.getAttribute(attribute)
