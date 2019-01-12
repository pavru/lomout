package net.pototskiy.apps.magemediation.dsl.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.dsl.config.ConfigDsl
import net.pototskiy.apps.magemediation.dsl.config.mediator.mage.MageMediatorConfigurationBuilder
import net.pototskiy.apps.magemediation.dsl.config.mediator.mapping.MappingConfigurationBuilder
import net.pototskiy.apps.magemediation.dsl.config.mediator.onec.OnecMediatorConfigurationBuilder
import net.pototskiy.apps.magemediation.api.config.mediator.MediatorConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.mage.MageMediatorConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.mapping.MappingConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.onec.OnecMediatorConfiguration

@ConfigDsl
class MediatorConfigurationBuilder {
    private var onecConf: OnecMediatorConfiguration? = null
    private var mageConf: MageMediatorConfiguration? = null
    private var mapping: MappingConfiguration? = null

    @Suppress("unused")
    fun MediatorConfigurationBuilder.onec(block: OnecMediatorConfigurationBuilder.()->Unit) {
        onecConf = OnecMediatorConfigurationBuilder().apply(block).build()
    }

    @Suppress("unused")
    fun MediatorConfigurationBuilder.magento(block: MageMediatorConfigurationBuilder.()->Unit) {
        mageConf = MageMediatorConfigurationBuilder().apply(block).build()
    }

    @Suppress("unused")
    fun MediatorConfigurationBuilder.mapping(block: MappingConfigurationBuilder.()->Unit) {
        mapping = MappingConfigurationBuilder().apply(block).build()
    }

    fun build(): MediatorConfiguration {
        return MediatorConfiguration(
            onecConf
                ?: throw ConfigException("Mediator section must include OneC configuration"),
            mageConf
                ?: throw ConfigException("Mediator section must include Magento configuration"),
            mapping ?: MappingConfigurationBuilder().build()
        )
    }
}