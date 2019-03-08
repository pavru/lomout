package net.pototskiy.apps.magemediation.api.config

import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.magemediation.api.CONFIG_LOG_NAME
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.enableScriptsInstancesSharing
import kotlin.script.experimental.api.onFailure
import kotlin.script.experimental.api.resultOrNull
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

class ConfigHost(private val configFile: File) {
    private val logger = LogManager.getLogger(CONFIG_LOG_NAME)
    private val scriptHost = BasicJvmScriptingHost()
    private var compiledScript: CompiledScript<*>? = null

    fun compile() {
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<ConfigScript>()
        val scriptHost = BasicJvmScriptingHost()
// TODO: 06.02.2019 Bug KT-29741, switch on cache after bug will be resolved
//        val scriptHost = BasicJvmScriptingHost(
//            compiler = JvmScriptCompiler(cache = FileBasedScriptCache(File("tmp/config/cache")))
//        )
        val compileResult = runBlocking {
            scriptHost.compiler(configFile.toScriptSource(), compilationConfiguration)
        }.onFailure { result ->
            result.reports.forEach {
                logMessage(
                    it.severity,
                    it.message,
                    it.sourcePath?.let { path -> File(path).name } ?: configFile.name,
                    it.location?.start?.line ?: 0
                )
                logger.trace("Exception:", it.exception)
                logger.trace("Caused:", it.exception?.cause)
            }
            if (result.reports.any {
                    it.severity in arrayOf(
                        ScriptDiagnostic.Severity.ERROR,
                        ScriptDiagnostic.Severity.FATAL
                    )
                }) {
                throw ConfigException("Configuration file can not be compiled")
            }
        }
        compiledScript = compileResult.resultOrNull()
    }

    fun evaluate(): Config {
        val result = runBlocking {
            compiledScript?.let { script ->
                scriptHost.evaluator(script, ScriptEvaluationConfiguration {
                    constructorArgs(emptyArray<String>())
                    enableScriptsInstancesSharing()
                }).onFailure { result ->
                    result.reports.forEach { diagnostic ->
                        ((diagnostic.exception?.cause) ?: diagnostic.exception)?.let {
                            val position = findExcretionPosition(it, File(diagnostic.sourcePath))
                            logMessage(
                                diagnostic.severity,
                                it.message ?: "",
                                File(diagnostic.sourcePath).name,
                                position?.line ?: 0
                            )
                        }
                            ?: logMessage(
                                diagnostic.severity,
                                diagnostic.message,
                                File(diagnostic.sourcePath).name,
                                diagnostic.location?.start?.line ?: 0
                            )
                        logger.trace(diagnostic.message, diagnostic.exception)
                    }
                    if (result.reports.any {
                            it.severity in arrayOf(
                                ScriptDiagnostic.Severity.ERROR,
                                ScriptDiagnostic.Severity.FATAL
                            )
                        }) {
                        throw ConfigException("Configuration file can not be evaluated")
                    }
                }
            }
        }
        val evaluatedConfig =
            ((result?.resultOrNull()?.returnValue as? ResultValue.Value)?.value as? ConfigScript)?.evaluatedConfig
        return evaluatedConfig ?: throw ConfigException("Config file can not be loaded")
    }

    private fun findExcretionPosition(throwable: Throwable, sourceFile: File): SourceCode.Position? {
        val callee = throwable.stackTrace.find { it.fileName == sourceFile.name }
        return callee?.let {
            SourceCode.Position(it.lineNumber, 0)
        }
    }

    private fun logMessage(severity: ScriptDiagnostic.Severity, message: String, file: String, line: Int) {
        val messageTemplate = "{} ({}:{})"
        when (severity) {
            ScriptDiagnostic.Severity.FATAL -> logger.fatal(messageTemplate, message, file, line)
            ScriptDiagnostic.Severity.ERROR -> logger.error(messageTemplate, message, file, line)
            ScriptDiagnostic.Severity.WARNING -> logger.warn(messageTemplate, message, file, line)
            ScriptDiagnostic.Severity.INFO -> logger.info(messageTemplate, message, file, line)
            ScriptDiagnostic.Severity.DEBUG -> logger.debug(messageTemplate, message, file, line)
        }
    }
}
