package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
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
class EntityTypeManager : EntityTypeManagerInterface {

    private val entityAttributes = mutableMapOf<EntityType, MutableMap<String, Attribute<*>>>()
    private val entities = mutableMapOf<String, EntityType>()
    private val entitySupers = mutableMapOf<EntityType, List<ParentEntityType>>()

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
        }
    }

    /**
     * Initial setup of entity type attributes
     *
     * @param entityType The entity type
     * @param attributes Attributes collection
     */
    override fun initialAttributeSetup(entityType: EntityType, attributes: AttributeCollection) {
        attributes.forEach { it.owner = entityType }
        if (attributes.any { it in entityType.attributes }) {
            val alreadyExists = attributes.filter { it in entityType.attributes }.joinToString(",") { it.fullName }
            throw AppConfigException(badPlace(entityType), "Attributes '$alreadyExists' are already defined.")
        }
        this.entityAttributes[entityType] = attributes.map { it.name to it }.toMap().toMutableMap()
    }

    /**
     * Get all entity type attributes
     *
     * @param entityType EntityType The entity type
     * @return AttributeCollection
     */
    override fun getEntityTypeAttributes(entityType: EntityType): AttributeCollection {
        val ownAttributes = entityAttributes[entityType]?.let { AttributeCollection(it.values.toList()) }
            ?: AttributeCollection(emptyList())
        val inheritedAttributes = ownAttributes.plus(entitySupers[entityType]?.map { inheritance ->
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
     * @param entityType EntityType The entity type
     * @param attributeName String The attribute name to get
     * @return Attribute<*>?
     */
    @Suppress("ReturnCount")
    override fun getEntityAttribute(entityType: EntityType, attributeName: String): Attribute<*>? {
        val ownAttribute = entityAttributes[entityType]?.get(attributeName)
        if (ownAttribute != null) return ownAttribute
        entitySupers[entityType]?.forEach { inheritance ->
            val inheritedAttribute = getEntityAttribute(inheritance.parent, attributeName).takeIf {
                it != null &&
                        (inheritance.include == null || inheritance.include.contains(it)) &&
                        (inheritance.exclude == null || !inheritance.exclude.contains(it))
            }
            if (inheritedAttribute != null) return inheritedAttribute
        }
        return null
    }

    /**
     * Add attributes to existing entity type. Only open entity allows this operation.
     *
     * @param entityType The entity type
     * @param attributes Attributes collection
     */
    override fun addEntityAttributes(entityType: EntityType, attributes: AttributeCollection) {
        checkThatAttributeIsNotAssigned(attributes)
        attributes.forEach { it.owner = entityType }
        checkEntityTypeIsOpen(entityType)
        checkEntityTypeHasNoAttributes(entityType, attributes)
        entityAttributes[entityType]?.putAll(attributes.map { it.name to it })
    }

    /**
     * Remove entity type
     *
     * @param entityType EntityType
     */
    override fun removeEntityType(entityType: EntityType) {
        entitySupers.remove(entityType)
        entityAttributes.remove(entityType)
        entities.remove(entityType.name)
    }

    /**
     * Create entity attribute without assigning to entity.
     *
     * @param T The attribute type
     * @param name The attribute name
     * @param typeClass The class of attribute type
     * @param block The attribute builder
     * @return Attribute<T>
     */
    override fun <T : Type> createAttribute(
        name: String,
        typeClass: KClass<out T>,
        block: EntityAttributeManagerInterface.Builder<T>.() -> Unit
    ): Attribute<T> = BuilderImpl(name, typeClass).apply(block).build()

    /**
     * Attribute builder class
     *
     * @param T The attribute type
     * @property key Key attribute
     * @property nullable Nullable attribute
     * @property auto An automatically created attribute
     * @property reader The attribute reader
     * @property writer The attribute writer
     * @property builder The attribute builder
     * @constructor
     * @param name String The attribute name
     * @param typeClass KClass<out T> The attribute type class
     */
    class BuilderImpl<T : Type>(
        name: String,
        typeClass: KClass<out T>
    ) : EntityAttributeManagerInterface.Builder<T>(name, typeClass) {
        private var key: Boolean = false
        private var nullable: Boolean = false
        private var auto: Boolean = false
        private var reader: AttributeReader<out T>? = null
        private var writer: AttributeWriter<out T>? = null
        private var builder: AttributeBuilder<out T>? = null

        /**
         * Mark the attribute as key one
         *
         * @param key Boolean?
         */
        override fun key(key: Boolean?) {
            if (key != null) this.key = key
        }

        /**
         * Mark the attribute as nullable one
         *
         * @param nullable Boolean?
         */
        override fun nullable(nullable: Boolean?) {
            if (nullable != null) this.nullable = nullable
        }

        /**
         * Mark the attribute as automatically created
         *
         * @param auto Boolean?
         */
        override fun auto(auto: Boolean?) {
            if (auto != null) this.auto = auto
        }

        /**
         * Set attribute reader
         *
         * @param reader AttributeReader<out T>?
         */
        override fun reader(reader: AttributeReader<out T>?) {
            if (reader != null) this.reader = reader
        }

        /**
         * Set attribute writer
         *
         * @param writer AttributeWriter<out T>?
         */
        override fun writer(writer: AttributeWriter<out T>?) {
            if (writer != null) this.writer = writer
        }

        /**
         * Set attribute builder
         *
         * @param builder AttributeBuilder<out T>?
         */
        override fun builder(builder: AttributeBuilder<out T>?) {
            this.builder = builder
        }

        /**
         * Build attribute
         *
         * @return Attribute<T>
         */
        @Suppress("UNCHECKED_CAST")
        override fun build(): Attribute<T> =
            object : Attribute<T>(
                name,
                typeClass,
                key,
                nullable,
                auto,
                reader
                    ?: defaultReaders[typeClass] as? AttributeReader<out T>
                    ?: throw AppConfigException(unknownPlace(), "Reader must be defined for the attribute '$name'."),
                writer
                    ?: defaultWriters[typeClass] as? AttributeWriter<out T>
                    ?: throw AppConfigException(unknownPlace(), "Writer must be defined for the attribute '$name'."),
                builder
            ) {}
    }

//    companion object : EntityTypeManagerCompanion()
}

private fun checkThatAttributeIsNotAssigned(attributes: AttributeCollection) {
    if (attributes.any { it.isAssigned }) {
        throw AppConfigException(
            badPlace(attributes.first { it.isAssigned }),
            "Attribute is already assigned to entity."
        )
    }
}

private fun checkEntityTypeHasNoAttributes(entityType: EntityType, attributes: AttributeCollection) {
    val entityAttributes = entityType.attributes
    if (attributes.any { it in entityAttributes }) {
        throw AppConfigException(
            badPlace(entityType),
            "Entity type already has attributes '${attributes.first { it in entityAttributes }}'."
        )
    }
}

private fun checkEntityTypeIsOpen(entityType: EntityType) {
    if (!entityType.open) {
        throw AppConfigException(badPlace(entityType), "Entity type is close, it's not possible to add an attribute.")
    }
}

/**
 * Add attributes to entity type
 *
 * @receiver EntityTypeManager
 * @param entityType EntityType
 * @param attributes List<Attribute<*>>
 */
fun EntityTypeManager.addEntityAttributes(entityType: EntityType, attributes: List<Attribute<*>>) =
    this.addEntityAttributes(entityType, AttributeCollection(attributes))

/**
 * Add the attribute to entity type
 *
 * @receiver EntityTypeManager
 * @param entityType EntityType
 * @param attribute Attribute<*>
 */
@PublicApi
fun EntityTypeManager.addEntityAttribute(entityType: EntityType, attribute: Attribute<*>) =
    this.addEntityAttributes(entityType, listOf(attribute))

/**
 * Get entity type by name
 *
 * @receiver EntityTypeManager
 * @param entityType String
 * @return EntityType
 */
operator fun EntityTypeManager.get(entityType: String) = this.getEntityType(entityType)
    ?: throw AppConfigException(unknownPlace(), "Entity type '$entityType' is not defined.")
