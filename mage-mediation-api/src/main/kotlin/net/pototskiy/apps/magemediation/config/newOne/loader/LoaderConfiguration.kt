package net.pototskiy.apps.magemediation.config.newOne.loader

import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.DatasetConfiguration

data class LoaderConfiguration (
    val files: List<DataFileConfiguration>,
    val datasets: List<DatasetConfiguration>
)
