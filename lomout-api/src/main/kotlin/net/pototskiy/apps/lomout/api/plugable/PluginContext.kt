package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object PluginContext : PluginContextInterface {
    override lateinit var config: Config
    override lateinit var entityTypeManager: EntityTypeManager
    override var logger: Logger = LogManager.getLogger(ROOT_LOG_NAME)
}
