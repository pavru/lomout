package net.pototskiy.apps.lomout.api.entity

import kotlin.reflect.KClass

/**
 * Entity attribute manager interface
 */
interface EntityAttributeManagerInterface {
    /**
     * Create attribute
     *
     * @param T Type The attribute type
     * @param name String The attribute name
     * @param typeClass KClass<out T> The attribute type class
     * @param block Builder<T>.() -> Unit The attribute builder
     * @return Attribute<T>
     */
    fun <T : Type> createAttribute(
        name: String,
        typeClass: KClass<out T>,
        block: Builder<T>.() -> Unit = {}
    ): Attribute<T>

    /**
     * Get all attributes of entity type
     *
     * @param entityType EntityType The entity type
     * @return AttributeCollection
     */
    fun getEntityTypeAttributes(entityType: EntityType): AttributeCollection

    /**
     * Get entity type attribute by name
     *
     * @param entityType EntityType The entity type
     * @param attributeName String The attribute name to get
     * @return Attribute<*>?
     */
    fun getEntityAttribute(entityType: EntityType, attributeName: String): Attribute<*>?

    /**
     * Attribute builder class
     *
     * @param T : Type The attribute class
     * @property name String The attribute name
     * @property typeClass KClass<out T> The attribute type class
     * @constructor
     */
    abstract class Builder<T : Type>(
        protected val name: String,
        protected val typeClass: KClass<out T>
    ) {
        /**
         * Mark attribute as key one
         *
         * @param key Boolean?
         */
        abstract fun key(key: Boolean?)

        /**
         * Mark attribute as nullable one
         *
         * @param nullable Boolean?
         */
        abstract fun nullable(nullable: Boolean?)

        /**
         * Mark attribute as auto created one
         *
         * @param auto Boolean?
         */
        abstract fun auto(auto: Boolean?)

        /**
         * Set attribute reader
         *
         * @param reader AttributeReader<out T>?
         */
        abstract fun reader(reader: AttributeReader<out T>?)

        /**
         * Set attribute writer
         *
         * @param writer AttributeWriter<out T>?
         */
        abstract fun writer(writer: AttributeWriter<out T>?)

        /**
         * Set attribute builder
         *
         * @param builder AttributeBuilder<out T>?
         */
        abstract fun builder(builder: AttributeBuilder<out T>?)

        /**
         * Build attribute
         *
         * @return Attribute<out T>
         */
        abstract fun build(): Attribute<out T>
    }
}
