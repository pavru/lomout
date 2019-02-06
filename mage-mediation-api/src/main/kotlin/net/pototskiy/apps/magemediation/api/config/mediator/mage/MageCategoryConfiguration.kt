package net.pototskiy.apps.magemediation.api.config.mediator.mage

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.type.Attribute
import net.pototskiy.apps.magemediation.api.config.type.AttributeLongType
import net.pototskiy.apps.magemediation.api.config.type.AttributeStringType
import net.pototskiy.apps.magemediation.api.config.type.AttributeTextType


data class MageCategoryConfiguration(
    val idAttribute: Attribute,
    val pathAttribute: Attribute
) {
    class Builder {
        private var idAttribute: Attribute? = null
        private var pathAttribute: Attribute? = null

        @Suppress("unused")
        fun Builder.idAttribute(name: String, block: Attribute.Builder.() -> Unit) {
            idAttribute = Attribute.Builder(
                name,
                listOf(
                    AttributeLongType::class,
                    AttributeStringType::class
                )
            ).apply(block).build()
        }

        @Suppress("unused")
        fun Builder.pathAttribute(name: String = "", block: Attribute.Builder.() -> Unit) {
            pathAttribute = Attribute.Builder(
                name,
                listOf(
                    AttributeStringType::class,
                    AttributeTextType::class
                )
            ).apply(block).build()
        }

        fun build(): MageCategoryConfiguration {
            return MageCategoryConfiguration(
                idAttribute ?: throw ConfigException("Magento medium id attribute must be configured"),
                pathAttribute ?: throw ConfigException("Magento medium path attribute must be configured")
            )
        }
    }
}
