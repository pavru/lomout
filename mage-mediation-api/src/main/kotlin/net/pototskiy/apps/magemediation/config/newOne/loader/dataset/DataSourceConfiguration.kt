package net.pototskiy.apps.magemediation.config.newOne.loader.dataset

import net.pototskiy.apps.magemediation.config.dataset.EmptyRowAction

data class DataSourceConfiguration(
    val fileId: String,
    val sheet: String,
    val emptyRowAction: EmptyRowAction
)
