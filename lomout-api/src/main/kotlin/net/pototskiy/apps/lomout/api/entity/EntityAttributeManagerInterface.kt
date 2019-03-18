package net.pototskiy.apps.lomout.api.entity

import kotlin.reflect.KClass

interface EntityAttributeManagerInterface {
    fun <T : Type> createAttribute(
        name: String,
        typeClass: KClass<out T>,
        block: Builder<T>.() -> Unit = {}
    ): Attribute<T>

    fun getEntityTypeAttributes(entityType: EntityType): AttributeCollection
    fun getEntityAttribute(entityType: EntityType, attributeName: String): Attribute<*>?

    abstract class Builder<T : Type>(
        protected val name: String,
        protected val typeClass: KClass<out T>
    ) {
        abstract fun key(key: Boolean?)
        abstract fun nullable(nullable: Boolean?)
        abstract fun auto(auto: Boolean?)
        abstract fun reader(reader: AttributeReader<out T>?)
        abstract fun writer(writer: AttributeWriter<out T>?)
        abstract fun builder(builder: AttributeBuilder<out T>?)
        abstract fun build(): Attribute<out T>
    }
}
