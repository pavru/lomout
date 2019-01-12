package net.pototskiy.apps.magemediation.dsl.config.mediator.onec

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.dsl.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.mediator.onec.OnecGroupMediatorConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.onec.OnecMediatorConfiguration

@ConfigDsl
class OnecMediatorConfigurationBuilder {
    private var groupConf: OnecGroupMediatorConfiguration? = null

    @Suppress("unused")
    fun OnecMediatorConfigurationBuilder.group(block: OnecGroupConfigurationBuilder.()->Unit) {
        groupConf = OnecGroupConfigurationBuilder().apply(block).build()
    }

    fun build(): OnecMediatorConfiguration {
        return OnecMediatorConfiguration(
            groupConf
                ?: throw ConfigException("OneC mediator configuration must include group configuration")
        )
    }
}
