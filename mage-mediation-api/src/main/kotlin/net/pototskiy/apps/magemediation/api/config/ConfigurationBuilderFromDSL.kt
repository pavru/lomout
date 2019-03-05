package net.pototskiy.apps.magemediation.api.config

import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import org.apache.logging.log4j.LogManager
import java.io.File

class ConfigurationBuilderFromDSL(private val configFile: File) {
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)!!

    private var configCache: Config? = null
    private val logger = LogManager.getLogger(STATUS_LOG_NAME)
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

    @Suppress("TooGenericExceptionCaught")
    private fun readConfig(): Config? {
        statusLog.info("Configuration loading has started from file: ${configFile.absolutePath}")
        var config: Config? = null
        try {
            val configHost = ConfigHost(configFile)
            configHost.compile()
            config = configHost.evaluate()
        } catch (e: Exception) {
            when (e) {
                is ConfigException -> logger.error(createConfigExceptionMessage(e))
                else -> logger.fatal("Config file error", e)
            }
            System.exit(1)
        }
        statusLog.info("Configuration loading has finished")
        return config
    }

    private fun createConfigExceptionMessage(e: ConfigException): String {
        return "${e.message}"
    }
}
