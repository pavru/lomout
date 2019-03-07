package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager

object PluginContext : PluginContextInterface {
    override lateinit var config: Config
    override lateinit var entityTypeManager: EntityTypeManager
}
