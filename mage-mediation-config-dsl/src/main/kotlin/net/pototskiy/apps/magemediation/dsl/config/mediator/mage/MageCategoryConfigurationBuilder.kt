package net.pototskiy.apps.magemediation.dsl.config.mediator.mage

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.dsl.config.ConfigDsl
import net.pototskiy.apps.magemediation.dsl.config.type.AttributeTypeBuilder
import net.pototskiy.apps.magemediation.api.config.mediator.mage.CategoryIDAttribute
import net.pototskiy.apps.magemediation.api.config.mediator.mage.CategoryPathAttribute
import net.pototskiy.apps.magemediation.api.config.mediator.mage.MageCategoryConfiguration
import net.pototskiy.apps.magemediation.api.config.type.AttributeIntType
import net.pototskiy.apps.magemediation.api.config.type.AttributeStringType
import net.pototskiy.apps.magemediation.api.config.type.AttributeTextType
import net.pototskiy.apps.magemediation.api.config.type.AttributeType
import net.pototskiy.apps.magemediation.api.plugable.medium.CategoryPathBuilder
import kotlin.reflect.KClass

@ConfigDsl
class MageCategoryConfigurationBuilder {
    private var idAttribute: CategoryIDAttribute? = null
    private var pathAttribute: CategoryPathAttribute? = null

    fun MageCategoryConfigurationBuilder.idAttribute(name: String, block: CategoryIDBuilder.() -> Unit) {
        idAttribute = CategoryIDBuilder(
            name
        ).apply(block).build()
    }

    @ConfigDsl
    class CategoryIDBuilder(private val name: String) {
        private var type: AttributeType? = null

        @Suppress("unused")
        fun CategoryIDBuilder.type(block: AttributeTypeBuilder.() -> Unit) {
            type = AttributeTypeBuilder(
                listOf(
                    AttributeIntType::class,
                    AttributeStringType::class
                )
            ).apply(block).build()
        }

        fun build(): CategoryIDAttribute {
            if (name.isEmpty()) {
                throw ConfigException("Category id attribute name must be configured and can not be empty")
            }
            return CategoryIDAttribute(
                name,
                type ?: AttributeIntType(
                    true,
                    DEFAULT_LOCALE
                )
            )
        }

    }

    @Suppress("unused")
    fun MageCategoryConfigurationBuilder.pathAttribute(name: String = "", block: PathAttributeBuilder.() -> Unit) {
        pathAttribute = PathAttributeBuilder(
            name
        ).apply(block).build()
    }

    fun build(): MageCategoryConfiguration {
        return MageCategoryConfiguration(
            idAttribute
                ?: throw ConfigException("Magento medium id attribute must be configured"),
            pathAttribute
                ?: throw ConfigException("Magento medium path attribute must be configured")
        )
    }

    @ConfigDsl
    class PathAttributeBuilder(private val name: String) {
        private var type: AttributeType? = null
        private var separator: Pair<String, String>? = null
        private var root: String? = null
        private var synthetic: Boolean = false
        private var builder: KClass<out CategoryPathBuilder>? = null

        @Suppress("unused")
        fun PathAttributeBuilder.type(block: AttributeTypeBuilder.() -> Unit) {
            type = AttributeTypeBuilder(
                listOf(
                    AttributeStringType::class,
                    AttributeTextType::class
                )
            )
                .apply(block)
                .build()
        }

        @Suppress("unused")
        fun PathAttributeBuilder.root(root: String) {
            this.root = root
        }

        @Suppress("unused")
        fun PathAttributeBuilder.separator(old: String, new: String) {
            this.separator = old to new
        }

        @Suppress("unused")
        fun PathAttributeBuilder.synthetic(block: PathBuilderBuilder.() -> Unit) {
            this.synthetic = true
            this.builder = PathBuilderBuilder()
                .apply(block).build()
        }

        fun build(): CategoryPathAttribute {
            if (!synthetic && name.isEmpty()) {
                throw ConfigException("Magento medium path attribute is not synthetic therefore attibute name must be configured")
            }
            return CategoryPathAttribute(
                name,
                type ?: AttributeStringType(
                    false,
                    DEFAULT_LOCALE
                ),
                separator ?: "/" to "",
                root ?: "",
                synthetic,
                builder
            )
        }
    }

    @ConfigDsl
    class PathBuilderBuilder {
        private var klass: KClass<out CategoryPathBuilder>? = null

        @Suppress("unused")
        fun PathBuilderBuilder.klass(klass: KClass<out CategoryPathBuilder>) {
            this.klass = klass
        }

        fun build(): KClass<out CategoryPathBuilder> {
            return klass
                ?: throw ConfigException("Synthetic path attribute must have builder class")
        }
    }
}
