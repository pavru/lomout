package net.pototskiy.apps.magemediation.config.dsl.mediator.onec

import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.dsl.ConfigDsl
import net.pototskiy.apps.magemediation.config.newOne.mediator.onec.OnecGroupMediatorConfiguration
import net.pototskiy.apps.magemediation.config.newOne.mediator.onec.OnecMediatorConfiguration

@ConfigDsl
class OnecMediatorConfigurationBuilder {
    private var groupConf: OnecGroupMediatorConfiguration? = null

    @Suppress("unused")
    fun OnecMediatorConfigurationBuilder.group(block: OnecGroupMediatorConfigurationBuilder.()->Unit) {
        groupConf = OnecGroupMediatorConfigurationBuilder().apply(block).build()
    }

    fun build(): OnecMediatorConfiguration {
        return OnecMediatorConfiguration(
            groupConf ?: throw ConfigException("OneC mediator configuration must include group configuration")
        )
    }
}
