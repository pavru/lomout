package net.pototskiy.apps.magemediation.config.dsl.mediator

import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.dsl.ConfigDsl
import net.pototskiy.apps.magemediation.config.dsl.mediator.mage.MageMediatorConfigurationBuilder
import net.pototskiy.apps.magemediation.config.dsl.mediator.onec.OnecMediatorConfigurationBuilder
import net.pototskiy.apps.magemediation.config.mediator.mage.MageMediatorConfiguration
import net.pototskiy.apps.magemediation.config.mediator.MediatorConfiguration
import net.pototskiy.apps.magemediation.config.mediator.onec.OnecMediatorConfiguration

@ConfigDsl
class MediatorConfigurationBuilder {
    private var onecConf: OnecMediatorConfiguration? = null
    private var mageConf: MageMediatorConfiguration? = null

    @Suppress("unused")
    fun MediatorConfigurationBuilder.onec(block: OnecMediatorConfigurationBuilder.()->Unit) {
        onecConf = OnecMediatorConfigurationBuilder().apply(block).build()
    }

    @Suppress("unused")
    fun MediatorConfigurationBuilder.magento(block: MageMediatorConfigurationBuilder.()->Unit) {
        mageConf = MageMediatorConfigurationBuilder().apply(block).build()
    }

    fun build(): MediatorConfiguration {
        return MediatorConfiguration(
            onecConf ?: throw ConfigException("Mediator section must include OneC configuration"),
            mageConf ?: throw ConfigException("Mediator section must include Magento configuration")
        )
    }
}