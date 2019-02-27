package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.reader.defaultReaders
import net.pototskiy.apps.magemediation.api.entity.writer.defaultWriters
import net.pototskiy.apps.magemediation.api.plugable.*
import kotlin.reflect.KClass

abstract class Attribute<T : Type>(
    val name: AttributeName,
    val valueType: KClass<out T>,
    val key: Boolean = false,
    val nullable: Boolean = false,
    val auto: Boolean = false,
    val reader: AttributeReader<out T>,
    val writer: AttributeWriter<out T>,
    val builder: AttributeBuilder<out T>? = null
) {
    val isSynthetic: Boolean = builder != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Attribute<*>) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name.fullName
    }

    @ConfigDsl
    class Builder<T : Type>(
        private val entityType: String,
        private var name: String,
        private val typeClass: KClass<out Type>
    ) {
        @Suppress("UNCHECKED_CAST")
        private var key: Boolean = false
        private var nullable: Boolean = false
        @ConfigDsl
        var builder: AttributeBuilder<out T>? = null
        @ConfigDsl
        var reader: AttributeReader<out T>? = null
        @ConfigDsl
        var writer: AttributeWriter<out T>? = null

        fun key() = this.let { key = true }
        fun nullable() = this.let { nullable = true }

        @JvmName("builder__function")
        fun builder(block: AttributeBuilderFunction<T>) {
            this.builder = AttributeBuilderWithFunction(block)
        }

        @JvmName("builder__plugin")
        inline fun <reified P : AttributeBuilderPlugin<T>> builder(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            this.builder = AttributeBuilderWithPlugin(P::class, block as (AttributeBuilderPlugin<T>.() -> Unit))
        }

        fun reader(block: AttributeReaderFunction<T>) {
            this.reader = AttributeReaderWithFunction(block)
        }

        inline fun <reified P : AttributeReaderPlugin<T>> reader(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            this.reader = AttributeReaderWithPlugin(P::class, block as (AttributeReaderPlugin<T>.() -> Unit))
        }

        fun writer(block: AttributeWriterFunction<T>) {
            this.writer = AttributeWriterWithFunction(block)
        }

        inline fun <reified P : AttributeWriterPlugin<T>> writer(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            this.writer = AttributeWriterWithPlugin(P::class, block as (AttributeWriterPlugin<T>.() -> Unit))
        }

        fun build(): Attribute<T> {
            validateKeyIsNotList()
            validateKeyIsNotNullable()
            @Suppress("UNCHECKED_CAST")
            return EntityAttributeManager.createAttribute(AttributeName(entityType, name),typeClass) {
                key(key)
                nullable(nullable)
                auto(false)
                reader(reader ?: defaultReaders[typeClass] as AttributeReader<out T>)
                writer(writer ?: defaultWriters[typeClass] as AttributeWriter<out T>)
                builder(builder)
            } as Attribute<T>
        }

        private fun validateKeyIsNotNullable() {
            if (key && nullable) {
                throw ConfigException("Key attribute can not be nullable")
            }
        }

        private fun validateKeyIsNotList() {
            if (key && (typeClass.isList() || builder != null)) {
                throw ConfigException("Key attribute can not have list type or plugins.builder")
            }
        }
    }
}

typealias AnyTypeAttribute = Attribute<out Type>