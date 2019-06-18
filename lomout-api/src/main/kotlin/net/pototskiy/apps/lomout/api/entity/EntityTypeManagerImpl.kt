package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.database.AttributeTable
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.entity.type.ATTRIBUTELIST
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.entity.type.isTypeOf
import net.pototskiy.apps.lomout.api.entity.type.table
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.unknownPlace
import kotlin.reflect.KClass

/**
 * Entity type manager
 *
 * It should be used to create entity type, entity type attributes. It also is used to find entity type
 * by the name, and it's an attribute.
 *
 * @property entityAttributes MutableMap<EntityType, MutableMap<String, Attribute<*>>>
 * @property entities MutableMap<String, EntityType>
 * @property entitySupers MutableMap<EntityType, List<ParentEntityType>>
 */
class EntityTypeManagerImpl : EntityTypeManager() {

    private val entityAttributes = mutableMapOf<EntityType, MutableMap<String, AnyTypeAttribute>>()
    private val cachedEntityAttributes = mutableMapOf<EntityType, AttributeCollection>()
    private val entities = mutableMapOf<String, EntityType>()
    private val entitySupers = mutableMapOf<EntityType, List<ParentEntityType>>()
    private val entityAttributeTables = mutableMapOf<EntityType, Array<AttributeTable<*>>>()

    /**
     * Find entity type by name
     *
     * @param name String The entity type name
     * @return EntityType?
     */
    override fun getEntityType(name: String): EntityType? = entities[name]

    /**
     * Create entity type name
     *
     * @param name The entity type name
     * @param supers The entity type parent (super) types
     * @param open Open flag, true — attributes can be added later
     * @return EntityType
     */
    override fun createEntityType(
        name: String,
        supers: List<ParentEntityType>,
        open: Boolean
    ): EntityType {
        return object : EntityType(name, open) {}.also {
            it.manager = this
            entities[it.name] = it
            entitySupers[it] = supers
            updateAttributeTables(it)
        }
    }

    /**
     * Initial setup of entity type attributes
     *
     * @param type The entity type
     * @param attributes Attributes collection
     */
    override fun initialAttributeSetup(type: EntityType, attributes: AttributeCollection) {
        attributes.forEach { it.owner = type }
        if (attributes.any { it in type.attributes }) {
            val alreadyExists = attributes.filter { it in type.attributes }.joinToString(",") { it.fullName }
            throw AppConfigException(badPlace(type), "Attributes '$alreadyExists' are already defined.")
        }
        this.entityAttributes[type] = attributes.map { it.name to it }.toMap().toMutableMap()
        cachedEntityAttributes[type] = generateEntityTypeAttributes(type)
        updateAttributeTables(type)
    }

    /**
     * Get all entity type attributes
     *
     * @param type The entity type
     * @return AttributeCollection
     */
    override fun getEntityTypeAttributes(type: EntityType): AttributeCollection =
        cachedEntityAttributes[type] ?: AttributeCollection(emptyList())

    /**
     * Create list of entity type attributes
     *
     * @param type The entity type
     * @return AttributeCollection
     */
    private fun generateEntityTypeAttributes(type: EntityType): AttributeCollection {
        val ownAttributes = entityAttributes[type]?.let {
            AttributeCollection(
                it.values.toList()
            )
        }
            ?: AttributeCollection(emptyList())
        val inheritedAttributes = ownAttributes.plus(entitySupers[type]?.map { inheritance ->
            getEntityTypeAttributes(inheritance.parent).filter {
                inheritance.include == null || inheritance.include.contains(it)
            }.filterNot {
                inheritance.exclude != null && inheritance.exclude.contains(it)
            }
        }?.flatten() ?: emptyList())
        return AttributeCollection(inheritedAttributes)
    }

    /**
     * Get entity type attribute by name
     *
     * @param type The entity type
     * @param attributeName The attribute name to get
     * @return Attribute<*>?
     */
    @Suppress("ReturnCount")
    override fun getEntityAttribute(type: EntityType, attributeName: String): Attribute<*>? {
        return cachedEntityAttributes[type]?.firstOrNull { it.name == attributeName }
    }

    /**
     * Add attributes to existing entity type. Only open entity allows this operation.
     *
     * @param type The entity type
     * @param attribute The attribute to add
     */
    override fun addEntityAttribute(type: EntityType, attribute: AnyTypeAttribute) {
        checkThatAttributeIsNotAssigned(attribute)
        checkEntityTypeIsOpen(type)
        checkEntityTypeHasNoAttribute(type, attribute)
        attribute.owner = type
        entityAttributes[type]?.put(attribute.name, attribute)
        cachedEntityAttributes[type] = generateEntityTypeAttributes(type)
        updateAttributeTables(type)
    }

    override fun addEntityExtAttribute(type: EntityType, attribute: AnyTypeAttribute) {
        checkThatAttributeIsNotAssigned(attribute)
        checkThatAttributeHasBuilder(attribute)
        checkEntityTypeHasNoAttribute(type, attribute)
        attribute.owner = type
        entityAttributes[type]?.put(attribute.name, attribute)
        cachedEntityAttributes[type] = generateEntityTypeAttributes(type)
        updateAttributeTables(type)
    }

    override fun removeEntityExtAttribute(type: EntityType, attribute: AnyTypeAttribute) {
        if (entityAttributes[type]?.remove(attribute.name) == null) {
            throw AppConfigException(badPlace(type) + attribute, "Entity type has no attribute")
        }
        cachedEntityAttributes[type] = generateEntityTypeAttributes(type)
        updateAttributeTables(type)
    }

    /**
     * Remove entity type
     *
     * @param type EntityType
     */
    override fun removeEntityType(type: EntityType) {
        entitySupers.remove(type)
        entityAttributes.remove(type)
        entities.remove(type.name)
        cachedEntityAttributes.remove(type)
        entityAttributeTables.remove(type)
    }

    override fun <T : Type> createAttribute(
        name: String,
        type: KClass<out T>,
        key: Boolean,
        nullable: Boolean,
        auto: Boolean,
        builder: AttributeBuilder<out T>?,
        reader: AttributeReader<out T>?,
        writer: AttributeWriter<out T>
    ): Attribute<T> = Attribute(name, type, key, nullable, auto, builder, reader, writer)

    override fun getEntityMainTable(type: EntityType): DbEntityTable = DbEntityTable

    override fun getEntityAttributeTables(type: EntityType): Array<AttributeTable<*>> {
        return entityAttributeTables[type]
            ?: throw AppConfigException(badPlace(type), "Entity type is not fully defined.")
    }

    private fun updateAttributeTables(type: EntityType) {
        entityAttributeTables[type] = type.attributes
            .filterNot { it.isSynthetic || it.type.isTypeOf<ATTRIBUTELIST>() }
            .map { it.type.table }
            .groupBy { it }
            .keys
            .toTypedArray()
    }
}

private fun checkThatAttributeIsNotAssigned(attribute: AnyTypeAttribute) {
    if (attribute.isAssigned) {
        throw AppConfigException(badPlace(attribute), "Attribute is already assigned to entity.")
    }
}

private fun checkThatAttributeHasBuilder(attribute: AnyTypeAttribute) {
    if (!attribute.isSynthetic) {
        throw AppConfigException(
            badPlace(attribute),
            "Only attribute with the builder can be used as extended attribute"
        )
    }
}

private fun checkEntityTypeHasNoAttribute(type: EntityType, attribute: AnyTypeAttribute) {
    val entityAttributeNames = type.attributes.map { it.name }
    if (attribute.name in entityAttributeNames) {
        throw AppConfigException(
            badPlace(type),
            "Entity type already has attributes '${attribute.name}'."
        )
    }
}

private fun checkEntityTypeIsOpen(type: EntityType) {
    if (!type.open) {
        throw AppConfigException(badPlace(type), "Entity type is close, it's not possible to add an attribute.")
    }
}

/**
 * Add attributes to entity type
 *
 * @receiver EntityTypeManagerImpl
 * @param entityType EntityType
 * @param attributes List<Attribute<*>>
 */
fun EntityTypeManager.addEntityAttributes(entityType: EntityType, attributes: List<Attribute<*>>) =
    this.addEntityAttributes(entityType, AttributeCollection(attributes))

/**
 * Add the attribute to entity type
 *
 * @receiver EntityTypeManagerImpl
 * @param type The entity type
 * @param attributes Attributes collection
 */
@PublicApi
fun EntityTypeManager.addEntityAttributes(type: EntityType, attributes: AttributeCollection) =
    attributes.forEach { this.addEntityAttribute(type, it) }

/**
 * Get entity type by name
 *
 * @receiver EntityTypeManagerImpl
 * @param typeName The entity type name
 * @return EntityType
 */
operator fun EntityTypeManager.get(typeName: String) = this.getEntityType(typeName)
    ?: throw AppConfigException(unknownPlace(), "Entity type '$typeName' is not defined.")
