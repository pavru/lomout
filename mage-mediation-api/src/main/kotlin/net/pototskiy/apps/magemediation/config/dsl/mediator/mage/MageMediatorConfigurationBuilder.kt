package net.pototskiy.apps.magemediation.config.dsl.mediator.mage

import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.newOne.mediator.mage.MageCategoryMediatorConfiguration
import net.pototskiy.apps.magemediation.config.newOne.mediator.mage.MageMediatorConfiguration

class MageMediatorConfigurationBuilder {
    private var categoryConf: MageCategoryMediatorConfiguration? = null

    fun MageMediatorConfigurationBuilder.category(block: MageCategoryMediatorConfigurationBuilder.()->Unit) {
        categoryConf = MageCategoryMediatorConfigurationBuilder().apply(block).build()
    }

    fun build(): MageMediatorConfiguration {
        return MageMediatorConfiguration(
            categoryConf ?: throw ConfigException("Magento mediator section must have category configuration")
        )
    }
}
