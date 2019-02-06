package net.pototskiy.apps.magemediation

import net.pototskiy.apps.magemediation.api.plugable.Plugin

class PluginConfiguration : Plugin<PluginConfiguration, Any>(emptyList(), { Any() }) {
    init {
        Plugin.config = CONFIG_BUILDER.config
    }
}
