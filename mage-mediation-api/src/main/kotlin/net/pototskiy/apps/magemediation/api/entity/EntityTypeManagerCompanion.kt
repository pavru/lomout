package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import kotlin.reflect.KClass

open class EntityTypeManagerCompanion : EntityTypeManagerInterface {
    private var backingManager: EntityTypeManager? = null
    var currentManager: EntityTypeManager
        set(value) {
            backingManager = value
        }
        get(): EntityTypeManager {
            return backingManager ?: throw DatabaseException("Entity type manager is not set yet")
        }

    override fun getEntityType(name: String): EntityType? =
        currentManager.getEntityType(name)

    override fun createEntityType(name: String, supers: List<ParentEntityType>, open: Boolean): EntityType =
        currentManager.createEntityType(name, supers, open)

    override fun initialAttributeSetup(entityType: EntityType, attributes: AttributeCollection) =
        currentManager.initialAttributeSetup(entityType, attributes)

    override fun addEntityAttributes(entityType: EntityType, attributes: AttributeCollection) =
        currentManager.addEntityAttributes(entityType, attributes)

    override fun removeEntityType(entityType: EntityType) =
        currentManager.removeEntityType(entityType)

    override fun <T : Type> createAttribute(
        name: String,
        typeClass: KClass<out T>,
        block: EntityAttributeManagerInterface.Builder<T>.() -> Unit
    ): Attribute<T> = currentManager.createAttribute(name, typeClass, block)

    override fun getEntityTypeAttributes(entityType: EntityType): AttributeCollection =
        currentManager.getEntityTypeAttributes(entityType)

    override fun getEntityAttribute(entityType: EntityType, attributeName: String): Attribute<*>? =
        currentManager.getEntityAttribute(entityType, attributeName)
}
