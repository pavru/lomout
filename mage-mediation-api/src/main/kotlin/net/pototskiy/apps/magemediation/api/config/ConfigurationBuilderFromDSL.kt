package net.pototskiy.apps.magemediation.api.config

import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import org.apache.logging.log4j.LogManager
import java.io.File
import javax.script.ScriptException

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
                is ScriptException -> logger.error(createScriptExceptionMessage(e))
                else -> logger.fatal("Config file error", e)
            }
            System.exit(1)
        }
        statusLog.info("Configuration loading has finished")
        return config
    }

    private fun createScriptExceptionMessage(e: ScriptException): String {
        val regex = Regex("${ConfigException::class.qualifiedName}: ")
        val firstLine = e.message?.lines()?.get(0)
        val lines = e.message?.lines() ?: listOf()
        var lineNumber = 0
        return if (firstLine != null && firstLine.contains(regex)) {
            val message = firstLine.replace(regex, "")
            for (line in lines) {
                if (line.contains(Regex("\\.kts:[0-9]*"))) {
                    val extractLine = Regex(".*\\.kts:([0-9]*).*")
                    lineNumber = extractLine.matchEntire(line)?.groupValues?.get(1)?.toInt() ?: 0
                    break
                }
            }
            "Configuration error: line<$lineNumber>: $message"
        } else {
            val error = firstLine?.toLowerCase()?.replace("error: ", "")
            "Configuration compilation error: ${error ?: e.message}"
        }
    }

    private fun createConfigExceptionMessage(e: ConfigException): String {
        val line = findLine(e)
        // TODO: 27.01.2019 add filename and line to message
        return "${e.message}"
    }

    private fun findLine(e: Exception): Int {
        return e.stackTrace.find { it.fileName?.endsWith(".kts") == true }?.lineNumber ?: 0
    }
}
