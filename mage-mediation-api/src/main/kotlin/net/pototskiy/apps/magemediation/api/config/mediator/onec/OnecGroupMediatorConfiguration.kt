package net.pototskiy.apps.magemediation.api.config.mediator.onec

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.AttributeLongType
import net.pototskiy.apps.magemediation.api.config.data.AttributeStringType
import net.pototskiy.apps.magemediation.api.config.data.AttributeTextType


data class OnecGroupMediatorConfiguration(
    val idAttribute: Attribute,
    val pathAttribute: Attribute
) {
    class Builder {
        private var idAttribute: Attribute? = null
        private var pathAttribute: Attribute? = null

        @Suppress("unused")
        fun Builder.idAttribute(
            attribute: String,
            block: Attribute.Builder.() -> Unit
        ) {
            idAttribute = Attribute.Builder(
                attribute,
                listOf(
                    AttributeLongType::class,
                    AttributeStringType::class,
                    AttributeTextType::class
                )
            ).apply(block).build()
        }

        @Suppress("unused")
        fun Builder.pathAttribute(
            attribute: String = "",
            block: Attribute.Builder.() -> Unit
        ) {
            pathAttribute = Attribute.Builder(
                attribute,
                listOf(
                    AttributeStringType::class,
                    AttributeTextType::class
                )
            ).apply(block).build()
        }


        fun build(): OnecGroupMediatorConfiguration {
            return OnecGroupMediatorConfiguration(
                idAttribute
                    ?: throw ConfigException("OneC group id(code) attribute must be configured"),
                pathAttribute
                    ?: throw ConfigException("OneC group path attribute must be configured")
            )
        }
    }
}
