package net.pototskiy.apps.magemediation.config

import org.apache.log4j.Logger

object Configuration {
    private var configCache: Config? = null
    private val logger = Logger.getLogger("import")
    val config: Config
        get() {
            val v = configCache
            return if (v != null) {
                v
            } else {
                configCache = readConfig()
                if (!validateConfig(configCache)) {
                    System.exit(1)
                }
                configCache as Config
            }
        }

    private fun validateConfig(config: Config?): Boolean {
        if (config == null) {
            logger.error("Configuration is not loaded")
            return false
        }
        return true
    }

}
