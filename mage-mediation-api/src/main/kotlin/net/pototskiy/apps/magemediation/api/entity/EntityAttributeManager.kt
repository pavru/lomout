package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.reader.defaultReaders
import net.pototskiy.apps.magemediation.api.entity.writer.defaultWriters
import kotlin.reflect.KClass

object EntityAttributeManager : EntityAttributeManagerInterface {
    override fun getAttribute(name: AttributeName): Attribute<*>? {
        val attr = attributeRegistry[name]
        if (attr != null) {
            return attr
        } else {
            val eType = EntityTypeManager.getEntityType(name.entityType)
                ?: return null
            return findInheritedAttribute(eType, name)
        }

    }

    private fun findInheritedAttribute(eType: EType, name: AttributeName): Attribute<*>? {
        eType.inheritances.forEach { inheritance ->
            val attr = AttributeName(inheritance.parent.type, name.attributeName)
            if (inheritance.include?.let { list -> attr in list.map { it.name } } != false
                && inheritance.exclude?.let { list -> attr !in list.map { it.name } } != false) {
                attributeRegistry[attr]?.let {
                    return it
                }
            }
        }
        eType.inheritances.forEach { inheritance ->
            findInheritedAttribute(inheritance.parent, name)?.let {
                return it
            }
        }
        return null
    }

    override fun <T : Type> createAttribute(
        name: AttributeName,
        typeClass: KClass<out T>,
        block: EntityAttributeManagerInterface.Builder<T>.() -> Unit
    ): Attribute<T> {
        return BuilderImpl(name, typeClass).apply(block).build().also {
            if (attributeRegistry.containsKey(it.name)) {
                throw ConfigException("Attribute<${it.name}> is already defined")
            }
            attributeRegistry[it.name] = it
        }
    }

    override fun removeAttribute(name: AttributeName) {
        // TODO: 21.02.2019 refine entity type
        attributeRegistry.remove(name)
            ?: ConfigException("Attribute<$name> can not be remove from registry, it's not created yet")
    }

    override fun removeAttribute(attribute: Attribute<*>) {
        // TODO: 21.02.2019 refine entity type
        attributeRegistry.remove(attribute.name)
            ?: ConfigException("Attribute<${attribute.name}> can not be remove from registry, it's not created yet")
    }

    fun cleanEntityAttributeConfiguration() {
        attributeRegistry.clear()
    }

    private val attributeRegistry = mutableMapOf<AttributeName, Attribute<*>>()

    class BuilderImpl<T : Type>(
        name: AttributeName,
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
        // TODO: 20.02.2019 Create default attribute plugins.reader and writer based on type
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
}

fun EType.findAttribute(name: String): Attribute<*>? =
    EntityAttributeManager.getAttribute(AttributeName(this.type, name))

