package net.pototskiy.apps.lomout.api.config

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.loader.LoaderConfiguration
import net.pototskiy.apps.lomout.api.config.mediator.MediatorConfiguration
import net.pototskiy.apps.lomout.api.config.printer.PrinterConfiguration
import net.pototskiy.apps.lomout.api.unknownPlace
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl

/**
 * Root element of configuration file
 *
 * @property entityTypeManager EntityTypeManagerImpl
 * @property database DatabaseConfig
 * @property loader LoaderConfiguration?
 * @property mediator MediatorConfiguration?
 * @property printer PrinterConfiguration?
 * @constructor
 */
data class Config(
    val entityTypeManager: EntityTypeManagerImpl,
    val database: DatabaseConfig,
    val loader: LoaderConfiguration?,
    val mediator: MediatorConfiguration?,
    val printer: PrinterConfiguration?
) {
    /**
     * Configuration root element builder class
     *
     * @property helper ConfigBuildHelper
     * @property database DatabaseConfig?
     * @property loader LoaderConfiguration?
     * @property mediator MediatorConfiguration?
     * @property printer PrinterConfiguration?
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var database: DatabaseConfig? = null
        private var loader: LoaderConfiguration? = null
        private var mediator: MediatorConfiguration? = null
        private var printer: PrinterConfiguration? = null

        /**
         * Database configuration
         *
         * ```
         * ...
         *  database {
         *      name("lomout")
         *      server {...}
         *  }
         * ...
         * ```
         * * name — MySql database name, **mandatory**
         * * [server][DatabaseConfig.Builder.server] — server configuration part, mandatory
         *
         * @see DatabaseConfig
         */
        @ConfigDsl
        fun database(block: DatabaseConfig.Builder.() -> Unit) {
            helper.pushScope("database")
            this.database = DatabaseConfig.Builder().apply(block).build()
            helper.popScope()
        }

        /**
         * Loader configuration
         *
         * ```
         * ...
         *  loader {
         *      files {...}
         *      entities {
         *          entity {...}
         *          entity {...}
         *          ...
         *      }
         *      loadEntity("entity type name") {...}
         *      loadEntity("entity type name") {...}
         *      ...
         *  }
         * ...
         * ```
         * * [files][LoaderConfiguration.Builder.files] — configure source files
         * * [entities][LoaderConfiguration.Builder.entities] — configure entities that will be loaded
         * * [loadEntity][LoaderConfiguration.Builder.loadEntity]
         *
         * @see LoaderConfiguration
         *
         * @param block The loader configuration
         */
        @ConfigDsl
        fun loader(block: LoaderConfiguration.Builder.() -> Unit) {
            helper.pushScope("loader")
            loader = LoaderConfiguration.Builder(helper).apply(block).build()
            helper.popScope()
        }

        /**
         * Mediator configuration
         *
         * @see MediatorConfiguration
         *
         * @param block The mediator configuration
         */
        @ConfigDsl
        fun mediator(block: MediatorConfiguration.Builder.() -> Unit) {
            helper.pushScope("mediator")
            mediator = MediatorConfiguration.Builder(helper).apply(block).build()
            helper.popScope()
        }

        /**
         * Printer configuration
         *
         * @see PrinterConfiguration
         *
         * @param block The printer configuration
         */
        @ConfigDsl
        fun printer(block: PrinterConfiguration.Builder.() -> Unit) {
            helper.pushScope("printer")
            this.printer = PrinterConfiguration.Builder(helper).also(block).build()
            helper.popScope()
        }

        /**
         * Build configuration
         *
         * @return Config
         */
        fun build(): Config {
            val realDatabase = database ?: DatabaseConfig.Builder().build()
            return Config(helper.typeManager, realDatabase, loader, mediator, printer)
        }
    }
}

/**
 * Root element of configuration
 *
 * ```
 * config {
 *      database {...}
 *      loader {...}
 *      mediator {...}
 *      printer {...}
 * }
 * ```
 * * [database][DatabaseConfig] — **mandatory**
 * * [loader][LoaderConfiguration] — optional
 * * [mediator][MediatorConfiguration] — optional
 * * [printer][PrinterConfiguration] — optional
 *
 * @see Config
 * @receiver Any
 * @param block The configuration
 */
fun Any.config(block: Config.Builder.() -> Unit) {
    val script = (this as? ConfigScript)
    if (script != null) {
        val helper = ConfigBuildHelper(EntityTypeManagerImpl())
        script.evaluatedConfig = Config.Builder(helper).apply(block).build()
    } else
        throw AppConfigException(unknownPlace(), "Wrong config script object type.")
}
