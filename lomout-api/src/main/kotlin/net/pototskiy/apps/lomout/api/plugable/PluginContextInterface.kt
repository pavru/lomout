package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import org.apache.logging.log4j.Logger
import java.io.File

/**
 * Plugin context
 *
 * @property config Config
 * @property logger Logger
 */
interface PluginContextInterface {
    /**
     * Context configuration
     */
    var config: Config
    /**
     * Context logger
     */
    var logger: Logger
    /**
     * Main script file
     */
    var scriptFile: File
    /**
     * Entity repository
     */
    var repository: EntityRepositoryInterface
}
