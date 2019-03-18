package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager

object PluginContext : PluginContextInterface {
    override lateinit var config: Config
    override lateinit var entityTypeManager: EntityTypeManager
}
