package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.entity.reader.defaultReaders
import net.pototskiy.apps.magemediation.api.entity.writer.defaultWriters
import kotlin.reflect.KClass

class EntityTypeManager : EntityTypeManagerInterface {

    private val entityAttributes = mutableMapOf<EntityType, MutableMap<String, Attribute<*>>>()
    private val attributes = entityAttributes.values.map { it.values }.flatten()
    private val entities = mutableMapOf<String, EntityType>()
    private val entitySupers = mutableMapOf<EntityType, List<ParentEntityType>>()

    override fun getEntityType(name: String): EntityType? = entities[name]

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

    override fun initialAttributeSetup(entityType: EntityType, attributes: AttributeCollection) {
        attributes.forEach { it.owner = entityType }
        if (attributes.any { it in this.attributes }) {
            val alreadyExists = attributes.filter { it in this.attributes }.joinToString(",") { it.fullName }
            throw DatabaseException("Attributes<$$alreadyExists> are already defined")
        }
        this.entityAttributes[entityType] = attributes.map { it.name to it }.toMap().toMutableMap()
    }

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

    override fun addEntityAttributes(entityType: EntityType, attributes: AttributeCollection) {
        checkThatAttributeIsNotAssigned(attributes)
        attributes.forEach { it.owner = entityType }
        checkEntityTypeIsOpen(entityType)
        checkEntityTypeHasNoAttributes(entityType, attributes)
        entityAttributes[entityType]?.putAll(attributes.map { it.name to it })
    }

    override fun removeEntityType(entityType: EntityType) {
        entitySupers.remove(entityType)
        entityAttributes.remove(entityType)
    }

    override fun <T : Type> createAttribute(
        name: String,
        typeClass: KClass<out T>,
        block: EntityAttributeManagerInterface.Builder<T>.() -> Unit
    ): Attribute<T> = BuilderImpl(name, typeClass).apply(block).build()

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

        override fun key(key: Boolean?) {
            if (key != null) this.key = key
        }

        override fun nullable(nullable: Boolean?) {
            if (nullable != null) this.nullable = nullable
        }

        override fun auto(auto: Boolean?) {
            if (auto != null) this.auto = auto
        }

        override fun reader(reader: AttributeReader<out T>?) {
            if (reader != null) this.reader = reader
        }

        override fun writer(writer: AttributeWriter<out T>?) {
            if (writer != null) this.writer = writer
        }

        override fun builder(builder: AttributeBuilder<out T>?) {
            this.builder = builder
        }

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
                    ?: throw ConfigException("Reader must be defined for attribute<$name>"),
                writer
                    ?: defaultWriters[typeClass] as? AttributeWriter<out T>
                    ?: throw ConfigException("Writer must be defined for attribute<$name"),
                builder
            ) {}
    }

    companion object : EntityTypeManagerCompanion()
}

private fun checkThatAttributeIsNotAssigned(attributes: AttributeCollection) {
    if (attributes.any { it.isAssigned }) {
        val assigned = attributes.filter { it.isAssigned }.joinToString(",") { it.fullName }
        throw DatabaseException("Attributes<$assigned> are already assigned to entity")
    }
}

private fun checkEntityTypeHasNoAttributes(entityType: EntityType, attributes: AttributeCollection) {
    val entityAttributes = entityType.attributes
    if (attributes.any { it in entityAttributes }) {
        val alreadyExists = attributes.filter { it in entityAttributes }.joinToString(",") { it.fullName }
        throw DatabaseException("Entity type<${entityType.name}> already has attributes<$alreadyExists>")
    }
}

private fun checkEntityTypeIsOpen(entityType: EntityType) {
    if (!entityType.open) {
        throw DatabaseException("It's not allowed to add attribute to entity type<${entityType.name}>")
    }
}

fun EntityTypeManager.addEntityAttributes(entityType: EntityType, attributes: List<Attribute<*>>) =
    this.addEntityAttributes(entityType, AttributeCollection(attributes))

@PublicApi
fun EntityTypeManager.addEntityAttribute(entityType: EntityType, attribute: Attribute<*>) =
    this.addEntityAttributes(entityType, listOf(attribute))

@PublicApi
fun EntityTypeManager.Companion.addEntityAttributes(entityType: EntityType, attributes: List<Attribute<*>>) =
    this.currentManager.addEntityAttributes(entityType, AttributeCollection(attributes))

@PublicApi
fun EntityTypeManager.Companion.addEntityAttribute(entityType: EntityType, attribute: Attribute<*>) =
    this.currentManager.addEntityAttributes(entityType, listOf(attribute))

operator fun EntityTypeManager.get(entityType: String) = this.getEntityType(entityType)
    ?: throw DatabaseException("Entity<$entityType> is not defined")

operator fun EntityTypeManager.Companion.get(entityType: String) =
    this.currentManager[entityType]

@PublicApi
fun EntityTypeManager.Companion.getEntityTypeOrNull(entityType: String) =
    this.currentManager.getEntityType(entityType)
