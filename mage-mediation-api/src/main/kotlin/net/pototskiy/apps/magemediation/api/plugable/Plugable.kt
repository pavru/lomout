package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.Config

interface Plugable {
    fun setPluginsConfig(configuration: Config) {
        config = configuration
    }

    companion object {
        var config: Config? = null
    }
}