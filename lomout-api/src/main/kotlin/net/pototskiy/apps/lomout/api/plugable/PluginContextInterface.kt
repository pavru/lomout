package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import org.apache.logging.log4j.Logger

interface PluginContextInterface {
    var config: Config
    var entityTypeManager: EntityTypeManager
    var logger: Logger
}
