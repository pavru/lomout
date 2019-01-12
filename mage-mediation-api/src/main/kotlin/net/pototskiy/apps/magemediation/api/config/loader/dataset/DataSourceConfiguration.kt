package net.pototskiy.apps.magemediation.api.config.loader.dataset

import net.pototskiy.apps.magemediation.api.config.EmptyRowAction

data class DataSourceConfiguration(
    val fileId: String,
    val sheet: String,
    val emptyRowAction: EmptyRowAction
)
