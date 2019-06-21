package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.database.AttributeTable
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import kotlin.reflect.KClass

/**
 * Entity type manager interface
 */
abstract class EntityTypeManager {
    /**
     * Get entity type by name
     *
     * @param name String The entity type name
     * @return EntityType?
     */
    abstract fun getEntityType(name: String): EntityType?

    /**
     * Create entity type
     *
     * @param name String The entity type
     * @param open Boolean Open flag
     * @return EntityType
     */
    abstract fun createEntityType(name: String, open: Boolean): EntityType

    /**
     * Initial entity type attributes setup
     *
     * @param type The entity type
     * @param attributes Attributes collection
     */
    abstract fun initialAttributeSetup(type: EntityType, attributes: AttributeCollection)

    /**
     * Add attributes to open entity type
     *
     * @param type EntityType The entity type
     * @param attribute The attribute to add
     */
    abstract fun addEntityAttribute(type: EntityType, attribute: AnyTypeAttribute)

    /**
     * Add an attribute with builder to type
     *
     * @param type The entity type
     * @param attribute The attribute to add, it must be the attribute with builder
     */
    abstract fun addEntityExtAttribute(type: EntityType, attribute: AnyTypeAttribute)

    /**
     * Remove the extension attribute from entity type
     *
     * @param type The entity type
     * @param attribute The attribute to remove
     */
    abstract fun removeEntityExtAttribute(type: EntityType, attribute: AnyTypeAttribute)

    /**
     * Remove entity type
     *
     * @param type EntityType The entity type
     */
    abstract fun removeEntityType(type: EntityType)

    /**
     * Create new entity attribute without assigment
     *
     * @param name The attribute name
     * @param type The attribute type
     * @param key The key attribute
     * @param nullable The nullable attribute
     * @param auto The auto attribute
     * @param builder The attribute builder
     * @param reader The attribute reader
     * @param writer The attribute writer
     * @return The new attribute
     */
    @Suppress("LongParameterList", "UNCHECKED_CAST", "kotlin:S107")
    @SuppressWarnings("kotlin:S107")
    abstract fun <T : Type> createAttribute(
        name: String,
        type: KClass<out T>,
        key: Boolean = false,
        nullable: Boolean = false,
        auto: Boolean = false,
        builder: AttributeBuilder<out T>? = null,
        reader: AttributeReader<out T>? = defaultReaders[type] as AttributeReader<out T>,
        writer: AttributeWriter<out T> = defaultWriters[type] as AttributeWriter<out T>
    ): Attribute<T>

    /**
     * Get all attributes of entity type
     *
     * @param type EntityType The entity type
     * @return AttributeCollection
     */
    abstract fun getEntityTypeAttributes(type: EntityType): AttributeCollection

    /**
     * Get main(not extended) entity type attributes
     *
     * @param type The entity type
     * @return AttributeCollection
     */
    abstract fun getEntityTypeMainAttributes(type: EntityType): AttributeCollection

    /**
     * Get extended entity type attributes
     *
     * @param type The entity type extended attributes
     * @return AttributeCollection
     */
    abstract fun getEntityTypeExtAttributes(type: EntityType): AttributeCollection

    /**
     * Get entity type attribute by name
     *
     * @param type EntityType The entity type
     * @param attributeName String The attribute name to get
     * @return Attribute<*>?
     */
    abstract fun getEntityAttribute(type: EntityType, attributeName: String): Attribute<*>?

    /**
     * Get entity main table
     *
     * @param type The entity type
     * @return DbEntityTable
     */
    internal abstract fun getEntityMainTable(type: EntityType): DbEntityTable

    /**
     * Get array of entity attribute tables
     * @param type The entity type
     * @return Array<AttributeTable<*>>
     */
    internal abstract fun getEntityAttributeTables(type: EntityType): Array<AttributeTable<*>>
}
