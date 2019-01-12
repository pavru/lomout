package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.loader.dataset.DatasetConfiguration

data class LoaderConfiguration (
    val files: List<DataFileConfiguration>,
    val datasets: List<DatasetConfiguration>
)
