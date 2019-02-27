package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.database.DatabaseException

object EntityTypeManager : EntityTypeManagerInterface {
    private val entityTypeRegistry = mutableMapOf<String, EType>()

    override fun getEntityType(type: String): EType? = entityTypeRegistry[type]

    override fun createEntityType(
        name: String,
        inheritances: List<ETypeInheritance>,
        attributes: AttributeCollection,
        open: Boolean
    ): EType {
        return object : EType(name, inheritances, attributes, open) {}.also {
            if (entityTypeRegistry.containsKey(name)) {
                throw ConfigException("Entity type<$name> is already defined")
            }
            entityTypeRegistry[name] = it
        }
    }

    override fun refineEntityAttributes(eType: String, attributes: AttributeCollection) {
        entityTypeRegistry[eType]?.let { refineEntityAttributes(it, attributes) }
    }

    override fun refineEntityAttributes(eType: EType, attributes: AttributeCollection) =
        attributes.forEach { eType.addAttribute(it) }

    override fun refineEntityAttributes(eType: String, attribute: Attribute<*>) {
        entityTypeRegistry[eType]?.addAttribute(attribute)
    }

    override fun refineEntityAttributes(eType: EType, attribute: Attribute<*>) =
        eType.addAttribute(attribute)

    override fun removeEntityType(eType: String) {
        entityTypeRegistry.remove(eType)
            ?: throw ConfigException("Entity eType<$eType> does not defined and can not be removed")
    }

    override fun removeEntityType(eType: EType) {
        entityTypeRegistry.remove(eType.type)
            ?: throw ConfigException("Entity type<${eType.type} does not defined and can not be removed")
    }

    fun cleanEntityTypeConfiguration() {
        EntityAttributeManager.cleanEntityAttributeConfiguration()
        entityTypeRegistry.clear()
    }
}

operator fun EType.Companion.get(type: String): EType = EntityTypeManager.getEntityType(type)
    ?: throw DatabaseException("Entity<$type> is not defined")

fun EType.Companion.getETypeOrNull(type: String) = EntityTypeManager.getEntityType(type)
