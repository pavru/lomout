package net.pototskiy.apps.magemediation.config

import org.apache.log4j.Logger
import java.io.File
import java.io.InputStream
import javax.script.Compilable
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class Configuration(private val input: InputStream) {

    constructor(file: String) : this(File(file).inputStream())

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

    private fun readConfig(): Config? {
        var config: Config? = null
        try {
            System.setProperty("idea.io.use.fallback", "true")
            val scriptEngine = ScriptEngineManager(Thread.currentThread().contextClassLoader)
                .getEngineByExtension("kts")
            input.reader().use {
                val compiledScript = (scriptEngine as Compilable).compile(it)
                config = compiledScript.eval() as Config
            }
        } catch (e: Exception) {
            when (e) {
                is ConfigException -> logger.error(createConfigExceptionMessage(e))
                is ScriptException -> logger.error(createScriptExceptionMessage(e))
                else -> logger.error("Internal error", e)
            }
            System.exit(1)
        }
        return config
    }

    private fun createScriptExceptionMessage(e: ScriptException): Any? {
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

    private fun createConfigExceptionMessage(e: ConfigException): Any? {
        val line = findLine(e)
        return "Configuration error: line<$line>: ${e.message}"
    }

    private fun findLine(e: Exception): Int {
        return e.stackTrace.find { it.fileName.endsWith(".kts") }?.lineNumber ?: 0
    }
}
