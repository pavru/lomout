package net.pototskiy.apps.magemediation.api.config

import net.pototskiy.apps.magemediation.api.AppConfigException
import net.pototskiy.apps.magemediation.api.config.loader.LoaderConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.MediatorConfiguration
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager

data class Config(
    val entityTypeManager: EntityTypeManager,
    val database: DatabaseConfig,
    val loader: LoaderConfiguration,
    val mediator: MediatorConfiguration
) {
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var database: DatabaseConfig? = null
        private var loader: LoaderConfiguration? = null
        private var mediator: MediatorConfiguration? = null

        /**
         * Configure database
         */
        @Suppress("unused")
        fun Builder.database(block: DatabaseConfig.Builder.() -> Unit) {
            helper.pushScope("database")
            this.database = DatabaseConfig.Builder().apply(block).build()
            helper.popScope()
        }

        @Suppress("unused")
        fun Builder.loader(block: LoaderConfiguration.Builder.() -> Unit) {
            helper.pushScope("loader")
            loader = LoaderConfiguration.Builder(helper).apply(block).build()
            helper.popScope()
        }

        @Suppress("unused")
        fun Builder.mediator(block: MediatorConfiguration.Builder.() -> Unit) {
            helper.pushScope("mediator")
            mediator = MediatorConfiguration.Builder(helper).apply(block).build()
            helper.popScope()
        }

        fun build(): Config {
            val realDatabase = database ?: DatabaseConfig.Builder().build()
            val realLoader = loader
                ?: throw AppConfigException("Loader section must be in configuration")
            val realMediator = mediator
                ?: throw AppConfigException("Mediator section must be in configuration")
            return Config(helper.typeManager, realDatabase, realLoader, realMediator)
        }

//        companion object : ConfigBuildHelper()
    }

//    companion object {
//        var config: Config? = null
//    }
}

fun Any.config(block: Config.Builder.() -> Unit) {
    val script = (this as? ConfigScript)
    if (script != null) {
        val helper = ConfigBuildHelper(EntityTypeManager())
        script.evaluatedConfig = Config.Builder(helper).apply(block).build()
    } else
        throw AppConfigException("Wrong config script object type")
}
