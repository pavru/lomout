package net.pototskiy.apps.magemediation.api.config

import net.pototskiy.apps.magemediation.api.config.loader.LoaderConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.MediatorConfiguration

data class Config(
    val database: DatabaseConfig,
    val loader: LoaderConfiguration,
    val mediator: MediatorConfiguration
) {
    @ConfigDsl
    class Builder {
        private var database: DatabaseConfig? = null
        private var loader: LoaderConfiguration? = null
        private var mediator: MediatorConfiguration? = null

        /**
         * Configure database
         */
        @Suppress("unused")
        fun Builder.database(block: DatabaseConfig.Builder.() -> Unit) {
            pushScope("database")
            this.database = DatabaseConfig.Builder().apply(block).build()
            popScope()
        }

        @Suppress("unused")
        fun Builder.loader(block: LoaderConfiguration.Builder.() -> Unit) {
            pushScope("loader")
            loader = LoaderConfiguration.Builder().apply(block).build()
            popScope()
        }

        @Suppress("unused")
        fun Builder.mediator(block: MediatorConfiguration.Builder.() -> Unit) {
            pushScope("mediator")
            mediator = MediatorConfiguration.Builder().apply(block).build()
            popScope()
        }

        fun build(): Config {
            val realDatabase = database ?: DatabaseConfig.Builder().build()
            val realLoader = loader
                ?: throw ConfigException("Loader section must be in configuration")
            val realMediator = mediator
                ?: throw ConfigException("Mediator section must be in configuration")
            return Config(realDatabase, realLoader, realMediator)
        }

        companion object : ConfigBuildHelper()
    }

    companion object {
        var config: Config? = null
    }
}

fun Any.config(block: Config.Builder.() -> Unit) {
    val script = (this as? ConfigScript)
    if (script != null) {
        Config.Builder.initConfigBuilder()
        script.evaluatedConfig = Config.Builder().apply(block).build()
    } else
        throw ConfigException("Wrong config script object type")
}

