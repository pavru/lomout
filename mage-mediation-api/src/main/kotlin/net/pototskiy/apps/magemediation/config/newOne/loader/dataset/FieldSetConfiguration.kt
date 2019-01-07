package net.pototskiy.apps.magemediation.config.newOne.loader.dataset

import net.pototskiy.apps.magemediation.config.dataset.FieldSetType

data class FieldSetConfiguration(
    val name: String,
    val type: FieldSetType,
    val fields: List<FieldConfiguration>
)
