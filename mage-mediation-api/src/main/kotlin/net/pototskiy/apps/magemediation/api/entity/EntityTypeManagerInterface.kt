package net.pototskiy.apps.magemediation.api.entity

interface EntityTypeManagerInterface : EntityAttributeManagerInterface {
    fun getEntityType(name: String): EntityType?

    fun createEntityType(name: String, supers: List<ParentEntityType>, open: Boolean): EntityType
    fun initialAttributeSetup(entityType: EntityType, attributes: AttributeCollection)

    fun addEntityAttributes(entityType: EntityType, attributes: AttributeCollection)
    fun removeEntityType(entityType: EntityType)
}
