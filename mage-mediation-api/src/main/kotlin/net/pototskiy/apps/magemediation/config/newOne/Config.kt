package net.pototskiy.apps.magemediation.config.newOne

import net.pototskiy.apps.magemediation.config.newOne.loader.LoaderConfiguration
import net.pototskiy.apps.magemediation.config.newOne.mediator.MediatorConfiguration

data class Config (
    val database: DatabaseConfig,
    val loader: LoaderConfiguration,
    val mediator: MediatorConfiguration
)