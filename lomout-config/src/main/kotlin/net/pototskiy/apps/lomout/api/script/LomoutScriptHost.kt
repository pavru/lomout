/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.pototskiy.apps.lomout.api.script

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
import kotlin.script.experimental.host.with
import kotlin.script.experimental.jvm.compilationCache
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
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
class LomoutScriptHost(
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
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<LomoutScriptTemplate> {
            ivyFile?.let { updateClasspath(dependenciesFromIvyFile(it)) }
        }
        val scriptHost = BasicJvmScriptingHost(
            defaultJvmScriptingHostConfiguration.with {
                jvm {
                    compilationCache(FileBasedScriptCache(File(cacheDir), doNotUseCache))
                }
            }
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
    fun evaluate(): ResultWithDiagnostics<LomoutScript> {
        return runBlocking {
            compiledScript?.let { script ->

                scriptHost.evaluator(script, ScriptEvaluationConfiguration {
                    constructorArgs(emptyArray<String>())
                    enableScriptsInstancesSharing()
                }).onFailure { result ->
                    result.reports.forEach { diagnostic ->
                        ((diagnostic.exception?.cause) ?: diagnostic.exception)?.let {
                            logEvaluationErrorFromThrowable(
                                it,
                                File(diagnostic.sourcePath ?: "").name,
                                diagnostic.severity
                            )
                        } ?: logMessage(
                            diagnostic.severity,
                            diagnostic.message,
                            File(diagnostic.sourcePath ?: "").name,
                            diagnostic.location?.start?.line ?: 0
                        )
                        logger.trace(diagnostic.message, diagnostic.exception)
                    }
                }.onSuccess {
                    val returnValue = it.returnValue
                    if (returnValue is ResultValue.Error) {
                        logEvaluationErrorFromThrowable(
                            returnValue.error,
                            """.*\.lomout\.kts""",
                            ScriptDiagnostic.Severity.ERROR
                        )
                        ResultWithDiagnostics.Failure()
                    } else {
                        val eConfig = (returnValue.scriptInstance as? LomoutScriptTemplate)?.lomoutScript
                        eConfig?.scriptClassLoader = returnValue.scriptClass?.java?.classLoader
                        eConfig?.asSuccess() ?: ResultWithDiagnostics.Failure()
                    }
                }
            } ?: ResultWithDiagnostics.Failure()
        }
    }

    private fun logEvaluationErrorFromThrowable(
        it: Throwable,
        filePattern: String,
        severity: ScriptDiagnostic.Severity
    ) {
        val (sourcePath, position) = findExceptionPosition(it, filePattern)
        logMessage(
            severity,
            it.message ?: "",
            File(sourcePath).name,
            position.line
        )
    }

    private fun findExceptionPosition(throwable: Throwable, sourceFilePattern: String): Pair<String, Position> {
        val callee = throwable.stackTrace.find { it.fileName?.matches(Regex(sourceFilePattern)) == true }
        return callee?.let {
            it.fileName to Position(it.lineNumber, 0)
        } ?: Pair("", Position(0, 0))
    }

    private fun logMessage(severity: ScriptDiagnostic.Severity, message: String, file: String, line: Int) {
        val messageTemplate = "{} ({}:{})"
        logger.log(severity.toLogLevel(), messageTemplate, message, file, line)
    }
}
