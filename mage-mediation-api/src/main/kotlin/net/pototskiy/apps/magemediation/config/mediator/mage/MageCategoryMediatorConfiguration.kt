package net.pototskiy.apps.magemediation.config.mediator.mage

import net.pototskiy.apps.magemediation.config.type.AttributeType


data class MageCategoryMediatorConfiguration(
    val pathAttributeName: String,
    val pathAttributeType: AttributeType,
    val pathRoot: String,
    val pathSeparator: String
)
