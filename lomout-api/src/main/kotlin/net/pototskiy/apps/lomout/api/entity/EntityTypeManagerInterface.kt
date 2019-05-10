package net.pototskiy.apps.lomout.api.entity

/**
 * Entity type manager interface
 */
interface EntityTypeManagerInterface : EntityAttributeManagerInterface {
    /**
     * Get entity type by name
     *
     * @param name String The entity type name
     * @return EntityType?
     */
    fun getEntityType(name: String): EntityType?

    /**
     * Create entity type
     *
     * @param name String The entity type
     * @param supers List<ParentEntityType> Entity type parent (super) types
     * @param open Boolean Open flag
     * @return EntityType
     */
    fun createEntityType(name: String, supers: List<ParentEntityType>, open: Boolean): EntityType

    /**
     * Initial entity type attributes setup
     *
     * @param entityType EntityType The entity type
     * @param attributes AttributeCollection The attributes collection
     */
    fun initialAttributeSetup(entityType: EntityType, attributes: AttributeCollection)

    /**
     * Add attributes to open entity type
     *
     * @param entityType EntityType The entity type
     * @param attributes AttributeCollection The attribute collection
     */
    fun addEntityAttributes(entityType: EntityType, attributes: AttributeCollection)

    /**
     * Remove entity type
     *
     * @param entityType EntityType The entity type
     */
    fun removeEntityType(entityType: EntityType)
}
