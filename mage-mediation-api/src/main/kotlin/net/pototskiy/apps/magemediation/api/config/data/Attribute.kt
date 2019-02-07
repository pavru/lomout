package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.database.PersistentEntity
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuildFunction
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuildPlugin
import net.pototskiy.apps.magemediation.api.plugable.PluginArgsBuilder
import kotlin.reflect.KClass

data class Attribute(
    val name: String,
    val type: AttributeType,
    val key: Boolean,
    val nullable: Boolean,
    val auto: Boolean,
    val builder: AttributeBuilder<PersistentEntity<*>, Any?>?
) {
    val isSynthetic: Boolean = builder != null

    class Builder(
        private var name: String,
        private val typeRestriction: List<KClass<out AttributeType>> = listOf()
    ) {

        private var type: AttributeType? = null
        private var key: Boolean = false
        private var nullable: Boolean = false
        private var builder: AttributeBuilder<PersistentEntity<*>, Any?>? = null

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
        fun <E : PersistentEntity<*>, R : Any?> Builder.withBuilder(
            builder: AttributeBuildPlugin<E, R>,
            block: PluginArgsBuilder.() -> Unit = {}
        ): Builder {
            val args = PluginArgsBuilder().apply(block).build()
            @Suppress("UNCHECKED_CAST")
            this.builder = AttributeBuilderPlugin(
                builder,
                args
            ) as AttributeBuilder<PersistentEntity<*>, Any?>
            return this
        }

        @Suppress("unused")
        fun <T : PersistentEntity<*>, R : Any?> Builder.withBuilder(builder: AttributeBuildFunction<T,R>): Builder {
            @Suppress("UNCHECKED_CAST")
            this.builder = AttributeBuilderFunction(builder) as AttributeBuilder<PersistentEntity<*>, Any?>
            return this
        }

        fun build(): Attribute {
            return Attribute(
                name,
                type ?: AttributeStringType(false),
                key,
                nullable,
                false,
                builder
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
