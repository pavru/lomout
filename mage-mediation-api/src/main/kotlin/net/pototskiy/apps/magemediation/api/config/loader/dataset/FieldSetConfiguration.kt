package net.pototskiy.apps.magemediation.api.config.loader.dataset

import net.pototskiy.apps.magemediation.api.config.FieldSetType

data class FieldSetConfiguration(
    val name: String,
    val type: FieldSetType,
    val fields: List<FieldConfiguration>
)
