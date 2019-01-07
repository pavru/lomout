package net.pototskiy.apps.magemediation.config.newOne.loader.dataset

import net.pototskiy.apps.magemediation.config.newOne.type.AttributeType


data class FieldConfiguration(
    val name: String,
    val column: Int,
    val regex: Regex?,
    val type: AttributeType,
    val keyField: Boolean,
    val nested: Boolean,
    val parent: String?,
    val optional: Boolean
)