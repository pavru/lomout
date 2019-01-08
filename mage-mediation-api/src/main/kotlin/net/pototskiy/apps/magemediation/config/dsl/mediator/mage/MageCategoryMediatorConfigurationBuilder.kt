package net.pototskiy.apps.magemediation.config.dsl.mediator.mage

import net.pototskiy.apps.magemediation.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.dsl.ConfigDsl
import net.pototskiy.apps.magemediation.config.dsl.type.AttributeTypeBuilder
import net.pototskiy.apps.magemediation.config.mediator.mage.MageCategoryMediatorConfiguration
import net.pototskiy.apps.magemediation.config.type.AttributeStringType
import net.pototskiy.apps.magemediation.config.type.AttributeTextType
import net.pototskiy.apps.magemediation.config.type.AttributeType

@ConfigDsl
class MageCategoryMediatorConfigurationBuilder {
    private var pathAttribute: String? = null
    private var pathAttributeType: AttributeType? = null
    private var root: String? = null
    private var separator: String? = null

    @Suppress("unused")
    fun MageCategoryMediatorConfigurationBuilder.pathAttribute(name: String, block: PathAttributeBuilder.() -> Unit) {
        this.pathAttribute = name
        PathAttributeBuilder().apply(block)
    }

    fun build(): MageCategoryMediatorConfiguration {
        return MageCategoryMediatorConfiguration(
            pathAttribute ?: throw ConfigException("Path attribute name must be configured"),
            pathAttributeType ?: AttributeStringType(
                false,
                DEFAULT_LOCALE
            ),
            root ?: "",
            separator ?: "/"
        )
    }

    @ConfigDsl
    inner class PathAttributeBuilder {

        @Suppress("unused")
        fun PathAttributeBuilder.type(block: AttributeTypeBuilder.() -> Unit) {
            this@MageCategoryMediatorConfigurationBuilder.pathAttributeType =
                    AttributeTypeBuilder(listOf(AttributeStringType::class, AttributeTextType::class)).apply(block)
                        .build()
        }

        @Suppress("unused")
        fun PathAttributeBuilder.root(root: String) {
            this@MageCategoryMediatorConfigurationBuilder.root = root
        }

        @Suppress("unused")
        fun PathAttributeBuilder.separator(separator: String) {
            this@MageCategoryMediatorConfigurationBuilder.separator = separator
        }
    }
}
