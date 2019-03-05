package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.database.DatabaseException

object EntityTypeManager : EntityTypeManagerInterface, EntityAttributeManagerInterface by EntityAttributeManager {
    private val entityTypeRegistry = mutableMapOf<String, EntityType>()

    override fun getEntityType(name: String): EntityType? = entityTypeRegistry[name]

    override fun createEntityType(
        name: String,
        inheritances: List<EntityTypeInheritance>,
        attributes: AttributeCollection,
        open: Boolean
    ): EntityType {
        return object : EntityType(name, inheritances, attributes, open) {}.also {
            if (entityTypeRegistry.containsKey(name)) {
                throw ConfigException("Entity type<$name> is already defined")
            }
            entityTypeRegistry[name] = it
        }
    }

    override fun refineEntityAttributes(name: String, attributes: AttributeCollection) {
        entityTypeRegistry[name]?.let { refineEntityAttributes(it, attributes) }
    }

    override fun refineEntityAttributes(entityType: EntityType, attributes: AttributeCollection) =
        attributes.forEach { entityType.addAttribute(it) }

    override fun refineEntityAttributes(name: String, attribute: Attribute<*>) {
        entityTypeRegistry[name]?.addAttribute(attribute)
    }

    override fun refineEntityAttributes(entityType: EntityType, attribute: Attribute<*>) =
        entityType.addAttribute(attribute)

    override fun removeEntityType(name: String) {
        entityTypeRegistry.remove(name)
            ?: throw ConfigException("Entity entityType<$name> does not defined and can not be removed")
    }

    override fun removeEntityType(entityType: EntityType) {
        entityTypeRegistry.remove(entityType.name)
            ?: throw ConfigException("Entity type<${entityType.name} does not defined and can not be removed")
    }

    fun cleanEntityTypeConfiguration() {
        EntityAttributeManager.cleanEntityAttributeConfiguration()
        entityTypeRegistry.clear()
    }
}

@PublicApi
operator fun EntityType.Companion.get(type: String): EntityType = EntityTypeManager.getEntityType(type)
    ?: throw DatabaseException("Entity<$type> is not defined")

@PublicApi
fun EntityType.Companion.getETypeOrNull(type: String) = EntityTypeManager.getEntityType(type)
