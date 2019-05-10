package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import org.apache.logging.log4j.Logger

/**
 * Plugin context
 *
 * @property config Config
 * @property entityTypeManager EntityTypeManager
 * @property logger Logger
 */
interface PluginContextInterface {
    /**
     * Context configuration
     */
    var config: Config
    /**
     * Context entity type manager
     */
    var entityTypeManager: EntityTypeManager
    /**
     * Context logger
     */
    var logger: Logger
}
