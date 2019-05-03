package net.pototskiy.apps.lomout.api.config

import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.CONFIG_LOG_NAME
import net.pototskiy.apps.lomout.api.log.toLogLevel
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.SourceCode.Position
import kotlin.script.experimental.api.asSuccess
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.enableScriptsInstancesSharing
import kotlin.script.experimental.api.onFailure
import kotlin.script.experimental.api.onSuccess
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.JvmScriptCompiler
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

class ConfigHost(
    private val configFile: File,
    private val cacheDir: String,
    private val doNotUseCache: Boolean
) {
    private val logger = LogManager.getLogger(CONFIG_LOG_NAME)
    private val scriptHost = BasicJvmScriptingHost()
    private var compiledScript: CompiledScript<*>? = null

    fun compile(): ResultWithDiagnostics<CompiledScript<*>> {
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<ConfigScript>()
        val scriptHost = BasicJvmScriptingHost(
            compiler = JvmScriptCompiler(cache = FileBasedScriptCache(File(cacheDir), doNotUseCache))
        )
        return runBlocking {
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
        }.onSuccess {
            compiledScript = it
            it.asSuccess()
        }
    }

    fun evaluate(): ResultWithDiagnostics<Config> {
        return runBlocking {
            compiledScript?.let { script ->

                scriptHost.evaluator(script, ScriptEvaluationConfiguration {
                    constructorArgs(emptyArray<String>())
                    enableScriptsInstancesSharing()
                }).onFailure { result ->
                    result.reports.forEach { diagnostic ->
                        logMessage(
                            diagnostic.severity,
                            diagnostic.message,
                            File(diagnostic.sourcePath).name,
                            diagnostic.location?.start?.line ?: 0
                        )
                        ((diagnostic.exception?.cause) ?: diagnostic.exception)?.let {
                            val position = findExceptionPosition(it, File(diagnostic.sourcePath))
                            logMessage(
                                diagnostic.severity,
                                it.message ?: "",
                                File(diagnostic.sourcePath).name,
                                position?.line ?: 0
                            )
                        }
                        logger.trace(diagnostic.message, diagnostic.exception)
                    }
                }.onSuccess {
                    val eConfig = ((it.returnValue as ResultValue.Value).value as? ConfigScript)?.evaluatedConfig
                    eConfig?.asSuccess() ?: ResultWithDiagnostics.Failure()
                }
            } ?: ResultWithDiagnostics.Failure()
        }
    }

    private fun findExceptionPosition(throwable: Throwable, sourceFile: File): SourceCode.Position? {
        val callee = throwable.stackTrace.find { it.fileName == sourceFile.name }
        return callee?.let {
            Position(it.lineNumber, 0)
        }
    }

    private fun logMessage(severity: ScriptDiagnostic.Severity, message: String, file: String, line: Int) {
        val messageTemplate = "{} ({}:{})"
        logger.log(severity.toLogLevel(), messageTemplate, message, file, line)
    }
}
