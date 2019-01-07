package net.pototskiy.apps.magemediation.config.newOne.mediator.onec

import net.pototskiy.apps.magemediation.config.newOne.type.AttributeType


data class OnecGroupMediatorConfiguration (
    val groupCodeAttributeName: String,
    val groupCodeAttributeType: AttributeType,
    val codeStructure: String,
    val subGroupFiller: String,
    val separator: String,
    val root: String
)