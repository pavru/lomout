package net.pototskiy.apps.magemediation.config.newOne.mediator.mage

import net.pototskiy.apps.magemediation.config.newOne.type.AttributeType


data class MageCategoryMediatorConfiguration(
    val pathAttributeName: String,
    val pathAttributeType: AttributeType,
    val pathRoot: String,
    val pathSeparator: String
)
