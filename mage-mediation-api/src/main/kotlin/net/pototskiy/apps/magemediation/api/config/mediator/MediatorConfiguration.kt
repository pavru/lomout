package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.mediator.mage.MageMediatorConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.mapping.MappingConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.onec.OnecMediatorConfiguration

data class MediatorConfiguration(
    val onec: OnecMediatorConfiguration,
    val magento: MageMediatorConfiguration,
    val mapping: MappingConfiguration
)
