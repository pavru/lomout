package net.pototskiy.apps.magemediation.config.mediator

import net.pototskiy.apps.magemediation.config.mediator.mage.MageMediatorConfiguration
import net.pototskiy.apps.magemediation.config.mediator.onec.OnecMediatorConfiguration

data class MediatorConfiguration(
    val onec: OnecMediatorConfiguration,
    val magento: MageMediatorConfiguration
)
