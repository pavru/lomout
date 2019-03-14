package net.pototskiy.apps.magemediation.api.config

import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.script.experimental.api.asSuccess
import kotlin.script.experimental.api.onFailure
import kotlin.script.experimental.api.onSuccess
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
        lateinit var evaluatedConfig: Config
        statusLog.info("Configuration loading has started from file: ${configFile.absolutePath}")
        val configHost = ConfigHost(configFile)
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

    private fun createConfigExceptionMessage(e: ConfigException): String {
        return "${e.message}"
    }
}
