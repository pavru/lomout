package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.type.Attribute

data class AttributeCollection(val attributes: List<Attribute>) : List<Attribute> by attributes {
    @ConfigDsl
    class Builder {
        private val attributes = mutableListOf<Attribute>()
        fun Builder.attribute(name: String, block: Attribute.Builder.() -> Unit) =
            attributes.add(Attribute.Builder(name).apply(block).build())

        fun build(): AttributeCollection = AttributeCollection(attributes)
    }
}
