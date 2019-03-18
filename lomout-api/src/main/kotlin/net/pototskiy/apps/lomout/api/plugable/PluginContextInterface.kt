package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager

interface PluginContextInterface {
    var config: Config
    var entityTypeManager: EntityTypeManager
}
