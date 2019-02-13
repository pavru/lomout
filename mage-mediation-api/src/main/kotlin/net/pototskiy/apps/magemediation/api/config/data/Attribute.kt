package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.database.PersistentEntity
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuilderFunction
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuilderPlugin
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

data class Attribute(
    val name: String,
    val type: AttributeType,
    val key: Boolean,
    val nullable: Boolean,
    val auto: Boolean,
    val builder: AttributeBuilder<PersistentEntity<*>, Any?>?
) {
    val isSynthetic: Boolean = builder != null

    @ConfigDsl
    class Builder(
        private var name: String,
        private val typeRestriction: List<KClass<out AttributeType>> = listOf()
    ) {

        private var type: AttributeType? = null
        private var key: Boolean = false
        private var nullable: Boolean = false
        @Suppress("PropertyName")
        var __builder: AttributeBuilder<PersistentEntity<*>, Any?>? = null

        @Suppress("unused")
        fun Builder.type(block: AttributeType.Builder.() -> Unit): Builder =
            apply { this.type = AttributeType.Builder(typeRestriction).apply(block).build() }

        @Suppress("unused")
        fun Builder.key(): Builder = apply { this.key = true }

        @Suppress("unused")
        fun Builder.nullable(): Builder {
            this.nullable = true
            return this
        }

        @Suppress("unused")
        @JvmName("with_builder__plugin")
        inline fun <reified P : AttributeBuilderPlugin<E, R>,
                E : PersistentEntity<*>,
                R : Any?> Builder.withBuilder(): Builder {
            @Suppress("UNCHECKED_CAST")
            __builder = AttributeBuilderWithPlugin(P::class) as AttributeBuilder<PersistentEntity<*>, Any?>
            return this
        }

        @Suppress("unused")
        @JvmName("with_builder__plugin__options")
        inline fun <reified P : AttributeBuilderPlugin<E, R>,
                O : NewPlugin.Options,
                E : PersistentEntity<*>,
                R : Any?> Builder.withBuilder(
            block: O.() -> Unit
        ): Builder {
            val plugin = P::class.createInstance()
            @Suppress("UNCHECKED_CAST") val options = (plugin.optionSetter() as O).apply(block)
            @Suppress("UNCHECKED_CAST")
            __builder = AttributeBuilderWithPlugin(P::class, options) as AttributeBuilder<PersistentEntity<*>, Any?>
            return this
        }

        @Suppress("unused")
        @JvmName("with_builder__function")
        fun <E : PersistentEntity<*>, R : Any?> Builder.withBuilder(block: AttributeBuilderFunction<E, R>): Builder {
            @Suppress("UNCHECKED_CAST")
            __builder = AttributeBuilderWithFunction(block) as AttributeBuilder<PersistentEntity<*>, Any?>
            return this
        }

        fun build(): Attribute {
            return Attribute(
                name,
                type ?: AttributeStringType(false),
                key,
                nullable,
                false,
                __builder
            )
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Attribute) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
