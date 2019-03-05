package net.pototskiy.apps.magemediation.api.entity

// TODO: 18.02.2019 define parameters
interface EntityTypeManagerInterface : EntityAttributeManagerInterface {
    fun getEntityType(name: String): EntityType?
    fun createEntityType(
        name: String,
        inheritances: List<EntityTypeInheritance>,
        attributes: AttributeCollection,
        open: Boolean
    ): EntityType

    fun refineEntityAttributes(name: String, attributes: AttributeCollection)
    fun refineEntityAttributes(entityType: EntityType, attributes: AttributeCollection)
    fun refineEntityAttributes(name: String, attribute: Attribute<*>)
    fun refineEntityAttributes(entityType: EntityType, attribute: Attribute<*>)
    fun removeEntityType(name: String)
    fun removeEntityType(entityType: EntityType)
}
