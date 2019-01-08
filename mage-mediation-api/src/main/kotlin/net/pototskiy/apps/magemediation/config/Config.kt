package net.pototskiy.apps.magemediation.config

import net.pototskiy.apps.magemediation.config.loader.LoaderConfiguration
import net.pototskiy.apps.magemediation.config.mediator.MediatorConfiguration

data class Config (
    val database: DatabaseConfig,
    val loader: LoaderConfiguration,
    val mediator: MediatorConfiguration
)