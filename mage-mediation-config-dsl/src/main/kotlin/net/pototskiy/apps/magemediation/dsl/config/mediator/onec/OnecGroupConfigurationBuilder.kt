package net.pototskiy.apps.magemediation.dsl.config.mediator.onec

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.dsl.config.ConfigDsl
import net.pototskiy.apps.magemediation.dsl.config.type.AttributeTypeBuilder
import net.pototskiy.apps.magemediation.api.config.mediator.onec.OnecGroupIDAttribute
import net.pototskiy.apps.magemediation.api.config.mediator.onec.OnecGroupMediatorConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.onec.OnecGroupPathAttribute
import net.pototskiy.apps.magemediation.api.config.type.AttributeIntType
import net.pototskiy.apps.magemediation.api.config.type.AttributeStringType
import net.pototskiy.apps.magemediation.api.config.type.AttributeTextType
import net.pototskiy.apps.magemediation.api.config.type.AttributeType
import net.pototskiy.apps.magemediation.api.plugable.medium.GroupPathBuilder
import kotlin.reflect.KClass

@ConfigDsl
class OnecGroupConfigurationBuilder {
    private var idAttribute: OnecGroupIDAttribute? = null
    private var pathAttribute: OnecGroupPathAttribute? = null

    @Suppress("unused")
    fun OnecGroupConfigurationBuilder.idAttribute(
        attribute: String,
        block: GroupIDBuilder.() -> Unit
    ) {
        idAttribute = GroupIDBuilder(
            attribute
        ).apply(block).build()
    }

    fun OnecGroupConfigurationBuilder.pathAttribute(
        attribute: String = "",
        block: OnecGroupPathBuilder.() -> Unit
    ) {
        pathAttribute = OnecGroupPathBuilder(
            attribute
        ).apply(block).build()
    }

    @ConfigDsl
    class OnecGroupPathBuilder(private val name: String) {
        private var type: AttributeType? = null
        private var separator: Pair<String, String>? = null
        private var root: String? = null
        private var synthetic: Boolean = false
        private var builder: KClass<out GroupPathBuilder>? = null

        @Suppress("unused")
        fun OnecGroupPathBuilder.type(block: AttributeTypeBuilder.() -> Unit) {
            type = AttributeTypeBuilder(
                listOf(
                    AttributeStringType::class,
                    AttributeTextType::class
                )
            ).apply(block).build()
        }

        @Suppress("unused")
        fun OnecGroupPathBuilder.separator(old: String, new: String) {
            this.separator = old to new
        }

        @Suppress("unused")
        fun OnecGroupPathBuilder.root(root: String) {
            this.root = root
        }

        fun OnecGroupPathBuilder.synthetic(block: PathBuilderBuilder.() -> Unit) {
            this.synthetic = true
            this.builder = PathBuilderBuilder()
                .apply(block).build()
        }

        fun build(): OnecGroupPathAttribute {
            if (!synthetic && name.isEmpty()) {
                throw ConfigException("OneC group path attribute is not synthetic therefore attibute name must be configured")
            }
            return OnecGroupPathAttribute(
                name,
                type ?: AttributeStringType(
                    false,
                    DEFAULT_LOCALE
                ),
                separator ?: "" to "/",
                root ?: "",
                synthetic,
                builder
            )
        }
    }

    @ConfigDsl
    class PathBuilderBuilder {
        private var klass: KClass<out GroupPathBuilder>? = null
        fun PathBuilderBuilder.klass(klass: KClass<out GroupPathBuilder>) {
            this.klass = klass
        }

        fun build(): KClass<out GroupPathBuilder> {
            return klass ?: throw ConfigException("Synthetic path attribute must have builder class")
        }

    }

    fun build(): OnecGroupMediatorConfiguration {
        return OnecGroupMediatorConfiguration(
            idAttribute
                ?: throw ConfigException("OneC group id(code) attribute must be configured"),
            pathAttribute
                ?: throw ConfigException("OneC group path attribute must be configured")
        )
    }

    @ConfigDsl
    class GroupIDBuilder(private val name: String) {
        private var type: AttributeType? = null

        @Suppress("unused")
        fun GroupIDBuilder.type(block: AttributeTypeBuilder.() -> Unit) {
            type = AttributeTypeBuilder(
                listOf(
                    AttributeIntType::class,
                    AttributeStringType::class,
                    AttributeTextType::class
                )
            ).apply(block).build()
        }

        fun build(): OnecGroupIDAttribute {
            return OnecGroupIDAttribute(
                name,
                type ?: AttributeStringType(
                    false,
                    DEFAULT_LOCALE
                )
            )
        }
    }
}
