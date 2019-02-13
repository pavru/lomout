package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.Config

object PluginContext: PluginContextInterface {
    override lateinit var config: Config
}
