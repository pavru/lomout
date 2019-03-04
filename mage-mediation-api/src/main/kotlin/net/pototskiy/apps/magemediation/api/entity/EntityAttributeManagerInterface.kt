package net.pototskiy.apps.magemediation.api.entity

import kotlin.reflect.KClass

// TODO: 18.02.2019 define parameters
interface EntityAttributeManagerInterface {
    fun <T : Type> createAttribute(
        name: AttributeName,
        typeClass: KClass<out T>,
        block: Builder<T>.() -> Unit = {}
    ): Attribute<T>

    fun getAttributeOrNull(name: AttributeName): Attribute<*>?

    abstract class Builder<T : Type>(
        protected val name: AttributeName,
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
