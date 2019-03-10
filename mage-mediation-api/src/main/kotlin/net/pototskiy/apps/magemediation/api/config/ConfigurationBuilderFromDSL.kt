package net.pototskiy.apps.magemediation.api.config

import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.system.exitProcess

class ConfigurationBuilderFromDSL(private val configFile: File) {
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)!!

    private var configCache: Config? = null
    private val logger = LogManager.getLogger(STATUS_LOG_NAME)
    val config: Config
        get() {
            return configCache ?: readConfig().also { configCache = it }
        }

    private fun readConfig(): Config {
        statusLog.info("Configuration loading has started from file: ${configFile.absolutePath}")
        val config = try {
            val configHost = ConfigHost(configFile)
            configHost.compile()
            configHost.evaluate()
        } catch (e: ConfigException) {
            logger.error(createConfigExceptionMessage(e))
            exitProcess(1)
        }
        statusLog.info("Configuration loading has finished")
        return config
    }

    private fun createConfigExceptionMessage(e: ConfigException): String {
        return "${e.message}"
    }
}
