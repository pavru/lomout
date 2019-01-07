package net.pototskiy.apps.magemediation.config.dsl

import net.pototskiy.apps.magemediation.LOG_NAME
import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.dsl.loader.LoaderConfigurationBuilder
import net.pototskiy.apps.magemediation.config.dsl.mediator.MediatorConfigurationBuilder
import net.pototskiy.apps.magemediation.config.newOne.Config
import net.pototskiy.apps.magemediation.config.newOne.DatabaseConfig
import net.pototskiy.apps.magemediation.config.newOne.loader.LoaderConfiguration
import net.pototskiy.apps.magemediation.config.newOne.mediator.MediatorConfiguration
import org.apache.log4j.Logger

@ConfigDsl
class ConfigBuilder {
    private val logger = Logger.getLogger(LOG_NAME)
    private var database: DatabaseConfig? = null
    private var loader: LoaderConfiguration? = null
    private var mediator: MediatorConfiguration? = null

    /**
     * Configure database
     */
    @Suppress("unused")
    fun ConfigBuilder.database(block: DatabaseConfigBuilder.() -> Unit) {
        database = DatabaseConfigBuilder().apply(block).build()
    }

    @Suppress("unused")
    fun ConfigBuilder.loader(block: LoaderConfigurationBuilder.() -> Unit) {
        loader = LoaderConfigurationBuilder().apply(block).build()
    }

    @Suppress("unused")
    fun ConfigBuilder.mediator(block: MediatorConfigurationBuilder.() -> Unit) {
        mediator = MediatorConfigurationBuilder().apply(block).build()
    }

    fun build(): Config {
        val realDatabase = database ?: DatabaseConfigBuilder().build()
        val realLoader = loader
            ?: throw ConfigException("Loader section must be in configuration")
        val realMediator = mediator
            ?: throw ConfigException("Mediator section must be in configuration")
        return Config(realDatabase, realLoader, realMediator)
    }
}

fun config(block: ConfigBuilder.() -> Unit) = ConfigBuilder().apply(block).build()

@DslMarker
annotation class ConfigDsl