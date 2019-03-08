package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager

interface PluginContextInterface {
    var config: Config
    var entityTypeManager: EntityTypeManager
}
