package net.pototskiy.apps.magemediation.api.config.mediator.mapping

import net.pototskiy.apps.magemediation.api.config.data.Transformer
import net.pototskiy.apps.magemediation.api.config.data.Attribute

data class AttributeWithTransformation(
    val attribute: Attribute,
    val transformer: Transformer<Any, Any>?
)
