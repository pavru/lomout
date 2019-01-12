package net.pototskiy.apps.magemediation.dsl.config.mediator.mage

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.mediator.mage.MageCategoryConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.mage.MageMediatorConfiguration

class MageMediatorConfigurationBuilder {
    private var categoryConf: MageCategoryConfiguration? = null

    @Suppress("unused")
    fun MageMediatorConfigurationBuilder.category(block: MageCategoryConfigurationBuilder.()->Unit) {
        categoryConf = MageCategoryConfigurationBuilder().apply(block).build()
    }

    fun build(): MageMediatorConfiguration {
        return MageMediatorConfiguration(
            categoryConf
                ?: throw ConfigException("Magento mediator section must have medium configuration")
        )
    }
}
