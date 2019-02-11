package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.NewTransformer

data class AttrMap(
    val attribute: Attribute,
    val transformer: NewTransformer<Any?, Any?>?
)
