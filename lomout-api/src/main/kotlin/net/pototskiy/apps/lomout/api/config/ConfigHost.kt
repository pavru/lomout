package net.pototskiy.apps.lomout.api.config

import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.CONFIG_LOG_NAME
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.log.toLogLevel
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.SourceCode.Position
import kotlin.script.experimental.api.asSuccess
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.enableScriptsInstancesSharing
import kotlin.script.experimental.api.onFailure
import kotlin.script.experimental.api.onSuccess
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.JvmScriptCompiler
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

/**
 * Config script host
 *
 * @property configFile The config DSL file
 * @property cacheDir The compiled config script cache directory
 * @property doNotUseCache True — do not cache script, false — cache script
 * @property ivyFile The ivy.xml file with additional dependencies
 * @property logger The configuration logger
 * @property scriptHost BasicJvmScriptingHost
 * @property compiledScript CompiledScript<*>?
 * @constructor
 */
class ConfigHost(
    private val configFile: File,
    private val cacheDir: String,
    private val doNotUseCache: Boolean,
    private val ivyFile: File? = null
) {
    private val logger = LogManager.getLogger(CONFIG_LOG_NAME)
    private val scriptHost = BasicJvmScriptingHost()
    private var compiledScript: CompiledScript<*>? = null

    /**
     * Compile config script
     *
     * @return ResultWithDiagnostics<CompiledScript<*>>
     */
    fun compile(): ResultWithDiagnostics<CompiledScript<*>> {
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<ConfigScript> {
            ivyFile?.let { file ->
                dependenciesFromIvyFile(file)
                    .takeIf { it.isNotEmpty() }
                    ?.let { updateClasspath(it) }
            }
        }
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
                logger.trace(message("message.word.exception"), it.exception)
                logger.trace(message("message.word.caused"), it.exception?.cause)
            }
        }.onSuccess {
            compiledScript = it
            it.asSuccess()
        }
    }

    /**
     * Evaluate config script
     *
     * @return ResultWithDiagnostics<Config>
     */
    fun evaluate(): ResultWithDiagnostics<Config> {
        return runBlocking {
            compiledScript?.let { script ->

                scriptHost.evaluator(script, ScriptEvaluationConfiguration {
                    constructorArgs(emptyArray<String>())
                    enableScriptsInstancesSharing()
                }).onFailure { result ->
                    result.reports.forEach { diagnostic ->
                        if (diagnostic.exception != null || diagnostic.exception?.cause != null) {
                            ((diagnostic.exception?.cause) ?: diagnostic.exception)?.let {
                                val position = findExceptionPosition(it, File(diagnostic.sourcePath ?: ""))
                                logMessage(
                                    diagnostic.severity,
                                    it.message ?: "",
                                    File(diagnostic.sourcePath ?: "").name,
                                    position?.line ?: 0
                                )
                            }
                        } else {
                            logMessage(
                                diagnostic.severity,
                                diagnostic.message,
                                File(diagnostic.sourcePath ?: "").name,
                                diagnostic.location?.start?.line ?: 0
                            )
                        }
                        logger.trace(diagnostic.message, diagnostic.exception)
                    }
                }.onSuccess {
                    val eConfig = ((it.returnValue as ResultValue.Value).value as? ConfigScript)?.evaluatedConfig
                    eConfig?.scriptClassLoader =
                        (it.returnValue as ResultValue.Value).scriptInstance::class.java.classLoader
                    eConfig?.asSuccess() ?: ResultWithDiagnostics.Failure()
                }
            } ?: ResultWithDiagnostics.Failure()
        }
    }

    private fun findExceptionPosition(throwable: Throwable, sourceFile: File): Position? {
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
