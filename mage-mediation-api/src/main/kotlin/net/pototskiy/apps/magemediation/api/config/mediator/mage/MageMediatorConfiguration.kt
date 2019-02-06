package net.pototskiy.apps.magemediation.api.config.mediator.mage

import net.pototskiy.apps.magemediation.api.config.ConfigException

data class MageMediatorConfiguration(
    val category: MageCategoryConfiguration
) {
    class Builder {
        private var categoryConf: MageCategoryConfiguration? = null

        @Suppress("unused")
        fun Builder.category(block: MageCategoryConfiguration.Builder.() -> Unit) {
            categoryConf = MageCategoryConfiguration.Builder().apply(block).build()
        }

        fun build(): MageMediatorConfiguration {
            return MageMediatorConfiguration(
                categoryConf
                    ?: throw ConfigException("Magento mediator section must have medium configuration")
            )
        }
    }
}
