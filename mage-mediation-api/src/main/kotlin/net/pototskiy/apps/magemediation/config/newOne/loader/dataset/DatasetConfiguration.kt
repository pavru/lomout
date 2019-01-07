package net.pototskiy.apps.magemediation.config.newOne.loader.dataset

import net.pototskiy.apps.magemediation.config.DatasetTarget

data class DatasetConfiguration(
    val name: String,
    val headersRow: Int,
    val rowsToSkip: Int,
    val maxAbsentDays: Int,
    val target: DatasetTarget,
    val sources: List<DataSourceConfiguration>,
    val fieldSets: List<FieldSetConfiguration>
)
