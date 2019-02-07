package net.pototskiy.apps.magemediation.api.config.mediator.mage

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.AttributeLongType
import net.pototskiy.apps.magemediation.api.config.data.AttributeStringType
import net.pototskiy.apps.magemediation.api.config.data.AttributeTextType


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
