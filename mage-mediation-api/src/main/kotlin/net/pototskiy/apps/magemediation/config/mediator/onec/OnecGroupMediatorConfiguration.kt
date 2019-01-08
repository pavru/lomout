package net.pototskiy.apps.magemediation.config.mediator.onec

import net.pototskiy.apps.magemediation.config.type.AttributeType


data class OnecGroupMediatorConfiguration (
    val groupCodeAttributeName: String,
    val groupCodeAttributeType: AttributeType,
    val codeStructure: String,
    val subGroupFiller: String,
    val separator: String,
    val root: String
)