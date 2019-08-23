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

import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.script.resolver.FilesAndIvyResolver
import org.jetbrains.kotlin.script.util.DependsOn
import org.jetbrains.kotlin.script.util.Import
import org.jetbrains.kotlin.script.util.Repository
import java.io.File
import java.io.Serializable
import kotlin.script.dependencies.ScriptContents
import kotlin.script.experimental.api.RefineScriptCompilationConfigurationHandler
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCollectedData
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptConfigurationRefinementContext
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.asDiagnostics
import kotlin.script.experimental.api.asSuccess
import kotlin.script.experimental.api.foundAnnotations
import kotlin.script.experimental.api.importScripts
import kotlin.script.experimental.host.FileBasedScriptSource
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvm.updateClasspath

/**
 * Config script configuration on annotations
 */
@Suppress("ReturnCount", "TooGenericExceptionCaught")
class KtsConfigurator : RefineScriptCompilationConfigurationHandler, Serializable {
    /**
     * Main configuration function
     *
     * @param context The script context
     * @return ResultWithDiagnostics<ScriptCompilationConfiguration>
     */
    override operator fun invoke(
        context: ScriptConfigurationRefinementContext
    ): ResultWithDiagnostics<ScriptCompilationConfiguration> {
        val resolver = FilesAndIvyResolver()
        val logger = MainAndIdeLogger()

        val diagnostics = arrayListOf<ScriptDiagnostic>()

        val annotations = context.collectedData
            ?.get(ScriptCollectedData.foundAnnotations)?.takeIf { it.isNotEmpty() }
            ?: return context.compilationConfiguration.asSuccess()

        val scriptLocation = (context.script as? FileBasedScriptSource)?.file
        val scriptBaseDir = scriptLocation?.parentFile
        val importedSources = getImportScripts(annotations, scriptBaseDir)

        val resolvedClassPath = try {
            val scriptContents = object : ScriptContents {
                override val annotations: Iterable<Annotation> =
                    annotations.filter { it is DependsOn || it is Repository }
                override val file: File? = null
                override val text: CharSequence? = null
            }
            resolver.resolve(
                scriptContents,
                emptyMap(),
                { _, _, _ -> },
                null
            ).get()?.classpath?.toList()
        } catch (e: Throwable) {
            @Suppress("SpreadOperator")
            return ResultWithDiagnostics.Failure(
                *diagnostics.toTypedArray(),
                e.asDiagnostics(path = context.script.locationId)
            )
        }

        val newConf = ScriptCompilationConfiguration(context.compilationConfiguration) {
            if (resolvedClassPath != null && resolvedClassPath.isNotEmpty()) {
                updateClasspath(resolvedClassPath)
                logger.trace(
                    message(
                        "message.trace.script.classpath.updated",
                        resolvedClassPath.joinToString(",") { it.absolutePath })
                )
            }
            if (importedSources.isNotEmpty()) {
                importScripts.append(importedSources)
                logger.trace(
                    message(
                        "message.trace.script.import.script",
                        context.script.name,
                        importedSources.joinToString(",") { it.name ?: "" })
                )
            }
        }
        return newConf.asSuccess(diagnostics)
    }

    private fun getImportScripts(
        annotations: List<Annotation>,
        scriptBaseDir: File?
    ): List<SourceCode> {
        return annotations.flatMap {
            (it as? Import)?.paths?.map { sourceName ->
                FileScriptSource(scriptBaseDir?.resolve(sourceName) ?: File(sourceName))
            } ?: emptyList()
        }
    }
}
