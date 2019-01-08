package net.pototskiy.apps.magemediation.config.loader.dataset

import net.pototskiy.apps.magemediation.config.EmptyRowAction

data class DataSourceConfiguration(
    val fileId: String,
    val sheet: String,
    val emptyRowAction: EmptyRowAction
)
