package net.pototskiy.apps.magemediation.api.config.mediator.onec

import net.pototskiy.apps.magemediation.api.config.ConfigException

data class OnecMediatorConfiguration (
    var group: OnecGroupMediatorConfiguration
) {
    class Builder {
        private var groupConf: OnecGroupMediatorConfiguration? = null

        @Suppress("unused")
        fun Builder.group(block: OnecGroupMediatorConfiguration.Builder.() -> Unit) {
            groupConf = OnecGroupMediatorConfiguration.Builder().apply(block).build()
        }

        fun build(): OnecMediatorConfiguration {
            return OnecMediatorConfiguration(
                groupConf
                    ?: throw ConfigException("OneC mediator configuration must include group configuration")
            )
        }
    }
}
