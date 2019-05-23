package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppAttributeException
import net.pototskiy.apps.lomout.api.Generated
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.NamedObject
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import net.pototskiy.apps.lomout.api.plugable.AttributeBuilderFunction
import net.pototskiy.apps.lomout.api.plugable.AttributeBuilderPlugin
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderFunction
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterFunction
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import kotlin.reflect.KClass

/**
 * Entity attribute
 *
 * @param T The attribute type
 * @property name The attribute name
 * @property valueType The class of attribute type
 * @property key Key attribute indicator
 * @property nullable Nullable attribute indicator
 * @property auto The attribute create automatically from source analysis
 * @property reader The attribute reader
 * @property writer The attribute write
 * @property builder The attribute builder
 * @property manager The owner entity type manager
 * @property owner The owner entity type
 * @property isAssigned Is assigned to entity type
 * @property fullName The attribute full name type_name:attr_name
 * @property isSynthetic Is a synthetic attribute, has the builder
 * @constructor
 */
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
    /**
     * Entity type manager
     */
    private lateinit var manager: EntityAttributeManagerInterface
    /**
     * Owner entity type
     */
    lateinit var owner: EntityType
    /**
     * Is assigned to entity type
     */
    val isAssigned: Boolean
        get() = ::owner.isInitialized
    /**
     * Attribute full name
     */
    val fullName: String
        get() = "${owner.name}:$name"
    /**
     * Attribute has builder
     */
    val isSynthetic: Boolean = builder != null

    /**
     * Are attributes equal
     *
     * @param other Any?
     * @return Boolean
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Attribute<*>) return false

        if (fullName != other.fullName) return false

        return true
    }

    /**
     * Attribute hash code
     *
     * @return Int
     */
    override fun hashCode(): Int = fullName.hashCode()

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = fullName

    /**
     * Attribute builder class
     *
     * @param T The attribute type
     * @property helper The config builder helper
     * @property name The attribute name
     * @property typeClass The class of attribute type
     * @property key Key attribute indicator
     * @property nullable Nullable attribute indicator
     * @property builder The attribute builder
     * @property reader The attribute reader
     * @property writer The attribute writer
     * @constructor
     */
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
        /**
         * Builder, do not use in DSL
         */
        var builder: AttributeBuilder<out T>? = null
        /**
         * Reader, do not use in DSL
         */
        var reader: AttributeReader<out T>? = null
        /**
         * Writer, do not use in DSL
         */
        var writer: AttributeWriter<out T>? = null

        /**
         * Attribute is key one
         *
         * ```
         * ...
         *  key()
         * ...
         * ```
         * * key() — mark attribute as key one, *optional*, **but at least one attribute must be key one**
         *
         */
        fun key() = this.let { key = true }

        /**
         * Attribute can have null value
         *
         * ```
         * ...
         *  nullable()
         * ...
         * ```
         * * nullable() — mark attribute as nullable, *optional*
         */
        fun nullable() = this.let { nullable = true }

        /**
         * Inline builder function, attribute can have only one builder
         *
         * ```
         * ...
         *  builder { entity ->
         *      // builder code
         *  }
         * ...
         * ```
         * @see AttributeBuilderFunction
         * @param block AttributeBuilderFunction<T>
         */
        @JvmName("builder__function")
        fun builder(block: AttributeBuilderFunction<T>) {
            this.builder = AttributeBuilderWithFunction(block)
        }

        /**
         * Attribute builder with a plugin, attribute can have only one builder
         *
         * ```
         * ...
         *  builder<BuilderPluginClass> {
         *      // builder options, it depends on builder class
         *  }
         * ...
         * ```
         * * [BuilderPluginClass][AttributeBuilderPlugin] — builder plugin class, **mandatory**
         *
         * @param P The builder plugin
         * @param block
         */
        @JvmName("builder__plugin")
        @Generated
        inline fun <reified P : AttributeBuilderPlugin<T>> builder(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            this.builder = AttributeBuilderWithPlugin(P::class, block as (AttributeBuilderPlugin<T>.() -> Unit))
        }

        /**
         * Inline attribute reader, attribute can have only one reader
         *
         * ```
         * ...
         *  reader { attribute, cell ->
         *      // reader code
         *  }
         * ...
         * ```
         * * [attribute][Attribute] — destination attribute for the reader
         * * [cell][net.pototskiy.apps.lomout.api.source.workbook.Cell] — cell to read value
         *
         * @see AttributeReaderFunction
         * @param block AttributeReaderFunction<T>
         */
        fun reader(block: AttributeReaderFunction<T>) {
            this.reader = AttributeReaderWithFunction(block)
        }

        /**
         * Attribute reader with a plugin, attribute can have only one reader
         *
         * ```
         * ...
         *  reader<ReaderPluginClass> {
         *      // plugin options, it depends on plugin class
         *  }
         * ...
         * ```
         * * [AttributeReaderPlugin][AttributeReaderPlugin] — reader plugin class, **mandatory**
         *
         * @param block
         */
        @Generated
        inline fun <reified P : AttributeReaderPlugin<T>> reader(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            this.reader = AttributeReaderWithPlugin(P::class, block as (AttributeReaderPlugin<T>.() -> Unit))
        }

        /**
         * Inline attribute writer function, attribute can have only one writer
         *
         * ```
         * ...
         *  writer { value, cell ->
         *      // writer code
         *  }
         * ...
         * ```
         * * [value][Type] — value to write to the cell
         * * [cell][net.pototskiy.apps.lomout.api.source.workbook.Cell] — cell to write value
         *
         * @see AttributeWriterFunction
         * @param block AttributeWriterFunction<T>
         */
        fun writer(block: AttributeWriterFunction<T>) {
            this.writer = AttributeWriterWithFunction(block)
        }

        /**
         * Attribute writer with a plugin, attribute can have only one writer
         *
         * ```
         * ...
         *  writer<WriterPluginClass> {
         *      // writer options, it depends on plugin class
         *  }
         * ...
         * ```
         * * [WriterPluginClass][AttributeWriterPlugin] — writer plugin class, **mandatory**
         *
         * @param block The writer options
         */
        @Generated
        inline fun <reified P : AttributeWriterPlugin<T>> writer(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            this.writer = AttributeWriterWithPlugin(P::class, block as (AttributeWriterPlugin<T>.() -> Unit))
        }

        /**
         * Build attribute
         *
         * @return Attribute<T>
         */
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
                throw AppAttributeException("Key attribute cannot be nullable")
            }
        }

        private fun validateKeyIsNotList() {
            if (key && (typeClass.isList() || builder != null)) {
                throw AppAttributeException("Key attribute cannot have list type or builder")
            }
        }
    }
}

/**
 * Any type attribute
 */
typealias AnyTypeAttribute = Attribute<out Type>
