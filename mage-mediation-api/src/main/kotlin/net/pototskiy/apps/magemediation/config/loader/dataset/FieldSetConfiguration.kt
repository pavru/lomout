package net.pototskiy.apps.magemediation.config.loader.dataset

import net.pototskiy.apps.magemediation.config.FieldSetType

data class FieldSetConfiguration(
    val name: String,
    val type: FieldSetType,
    val fields: List<FieldConfiguration>
)
