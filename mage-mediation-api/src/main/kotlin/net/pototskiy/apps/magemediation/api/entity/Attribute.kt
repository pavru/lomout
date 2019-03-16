package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.AppAttributeException
import net.pototskiy.apps.magemediation.api.Generated
import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.NamedObject
import net.pototskiy.apps.magemediation.api.entity.reader.defaultReaders
import net.pototskiy.apps.magemediation.api.entity.writer.defaultWriters
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuilderFunction
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuilderPlugin
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderFunction
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterFunction
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import kotlin.reflect.KClass

abstract class Attribute<T : Type>(
    override val name: String,
    val valueType: KClass<out T>,
    val key: Boolean = false,
    val nullable: Boolean = false,
    val auto: Boolean = false,
    val reader: AttributeReader<out T>,
    val writer: AttributeWriter<out T>,
    val builder: AttributeBuilder<out T>? = null
) : NamedObject {
    lateinit var manager: EntityAttributeManagerInterface
    lateinit var owner: EntityType
    val isAssigned: Boolean
        get() = ::owner.isInitialized
    val fullName: String
        get() = "${owner.name}:$name"
    val isSynthetic: Boolean = builder != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Attribute<*>) return false

        if (fullName != other.fullName) return false

        return true
    }

    override fun hashCode(): Int = fullName.hashCode()
    override fun toString(): String = fullName

    @Suppress("TooManyFunctions")
    @ConfigDsl
    class Builder<T : Type>(
        private val helper: ConfigBuildHelper,
        private var name: String,
        private val typeClass: KClass<out T>
    ) {
        @Suppress("UNCHECKED_CAST")
        private var key: Boolean = false
        private var nullable: Boolean = false
        var builder: AttributeBuilder<out T>? = null
        var reader: AttributeReader<out T>? = null
        var writer: AttributeWriter<out T>? = null

        fun key() = this.let { key = true }
        fun nullable() = this.let { nullable = true }

        @JvmName("builder__function")
        fun builder(block: AttributeBuilderFunction<T>) {
            this.builder = AttributeBuilderWithFunction(block)
        }

        @JvmName("builder__plugin")
        @Generated
        inline fun <reified P : AttributeBuilderPlugin<T>> builder(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            this.builder = AttributeBuilderWithPlugin(P::class, block as (AttributeBuilderPlugin<T>.() -> Unit))
        }

        fun reader(block: AttributeReaderFunction<T>) {
            this.reader = AttributeReaderWithFunction(block)
        }

        @Generated
        inline fun <reified P : AttributeReaderPlugin<T>> reader(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            this.reader = AttributeReaderWithPlugin(P::class, block as (AttributeReaderPlugin<T>.() -> Unit))
        }

        fun writer(block: AttributeWriterFunction<T>) {
            this.writer = AttributeWriterWithFunction(block)
        }

        @Generated
        inline fun <reified P : AttributeWriterPlugin<T>> writer(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            this.writer = AttributeWriterWithPlugin(P::class, block as (AttributeWriterPlugin<T>.() -> Unit))
        }

        fun build(): Attribute<T> {
            validateKeyIsNotList()
            validateKeyIsNotNullable()
            @Suppress("UNCHECKED_CAST")
            return helper.typeManager.createAttribute(name, typeClass) {
                key(key)
                nullable(nullable)
                auto(false)
                reader(reader ?: defaultReaders[typeClass] as AttributeReader<out T>)
                writer(writer ?: defaultWriters[typeClass] as AttributeWriter<out T>)
                builder(builder)
            }
        }

        private fun validateKeyIsNotNullable() {
            if (key && nullable) {
                throw AppAttributeException("Key attribute can not be nullable")
            }
        }

        private fun validateKeyIsNotList() {
            if (key && (typeClass.isList() || builder != null)) {
                throw AppAttributeException("Key attribute can not have list type or builder")
            }
        }
    }
}

typealias AnyTypeAttribute = Attribute<out Type>
