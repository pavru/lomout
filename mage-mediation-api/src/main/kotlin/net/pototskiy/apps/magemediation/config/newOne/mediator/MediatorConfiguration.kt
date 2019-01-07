package net.pototskiy.apps.magemediation.config.newOne.mediator

import net.pototskiy.apps.magemediation.config.newOne.mediator.mage.MageMediatorConfiguration
import net.pototskiy.apps.magemediation.config.newOne.mediator.onec.OnecMediatorConfiguration

data class MediatorConfiguration(
    val onec: OnecMediatorConfiguration,
    val magento: MageMediatorConfiguration
)
