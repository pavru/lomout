package net.pototskiy.apps.lomout.api.config

import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.script.experimental.api.asSuccess
import kotlin.script.experimental.api.onFailure
import kotlin.script.experimental.api.onSuccess
import kotlin.system.exitProcess

/**
 * Configuration builder from DSL file
 *
 * @property configFile The configuration DSL file
 * @property cacheDir The directory to cache compiled configuration
 * @property doNotUseCache True - no not cache script, false - cache script
 * @property statusLog The status logger
 * @property configCache The backing config field
 * @property logger The configuration logger
 * @property config The built config
 * @constructor
 */
class ConfigurationBuilderFromDSL(
    private val configFile: File,
    private val cacheDir: String = "tmp/config/cache",
    private val doNotUseCache: Boolean = false
) {
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)!!

    private var configCache: Config? = null
    private val logger = LogManager.getLogger(STATUS_LOG_NAME)
    val config: Config
        get() {
            return configCache ?: readConfig().also { configCache = it }
        }

    private fun readConfig(): Config {
        lateinit var evaluatedConfig: Config
        statusLog.info("Configuration loading has started from file: ${configFile.absolutePath}")
        val ivyFile = getIvyFile(configFile)
        val configHost = ConfigHost(configFile, cacheDir, doNotUseCache, ivyFile)
        configHost.compile().onFailure {
            logger.error("Configuration file can not be compiled")
        }.onSuccess { compileResult ->
            configHost.evaluate().onFailure {
                logger.error("Script can not be evaluated")
                exitProcess(1)
            }.onSuccess { evaluationResult ->
                evaluatedConfig = evaluationResult
                evaluationResult.asSuccess()
            }
            compileResult.asSuccess()
        }
        statusLog.info("Configuration loading has finished")
        return evaluatedConfig
    }

    private fun getIvyFile(configFile: File): File? {
        val ivyFile = configFile.parentFile.resolve("ivy.xml")
        return if (ivyFile.exists()) {
            ivyFile
        } else {
            null
        }
    }
}
