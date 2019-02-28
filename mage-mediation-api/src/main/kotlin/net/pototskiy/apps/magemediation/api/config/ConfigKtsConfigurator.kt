package net.pototskiy.apps.magemediation.api.config

import org.jetbrains.kotlin.script.util.DependsOn
import org.jetbrains.kotlin.script.util.Import
import org.jetbrains.kotlin.script.util.KotlinAnnotatedScriptDependenciesResolver
import org.jetbrains.kotlin.script.util.Repository
import org.jetbrains.kotlin.script.util.resolvers.DirectResolver
import java.io.File
import kotlin.script.dependencies.ScriptContents
import kotlin.script.dependencies.ScriptDependenciesResolver
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvm.compat.mapLegacyDiagnosticSeverity
import kotlin.script.experimental.jvm.compat.mapLegacyScriptPosition
import kotlin.script.experimental.jvm.updateClasspath

class ConfigKtsConfigurator : RefineScriptCompilationConfigurationHandler {
    private val resolver = KotlinAnnotatedScriptDependenciesResolver(
        emptyList(),
        arrayListOf(DirectResolver(), GradleCacheResolver(), LocalMavenResolver()).asIterable()
    )

    override operator fun invoke(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
        val diagnostics = arrayListOf<ScriptDiagnostic>()
        fun report(
            severity: ScriptDependenciesResolver.ReportSeverity,
            message: String,
            position: ScriptContents.Position?
        ) {
            diagnostics.add(
                ScriptDiagnostic(
                    message,
                    mapLegacyDiagnosticSeverity(severity),
                    context.script.locationId,
                    mapLegacyScriptPosition(position)
                )
            )
        }

        val annotations = context.collectedData?.get(ScriptCollectedData.foundAnnotations)?.takeIf { it.isNotEmpty() }
            ?: return context.compilationConfiguration.asSuccess()

        val scriptBaseDir = (context.script as? FileScriptSource)?.file?.absoluteFile?.parentFile
        val importedSources = annotations.flatMap {
            (it as? Import)?.paths?.map { sourceName ->
                FileScriptSource(scriptBaseDir?.resolve(sourceName) ?: File(sourceName))
            } ?: emptyList()
        }

        val resolvedClassPath = try {
            val scriptContents = object : ScriptContents {
                override val annotations: Iterable<Annotation> =
                    annotations.filter { it is DependsOn || it is Repository }
                override val file: File? = null
                override val text: CharSequence? = null
            }
            resolver.resolve(scriptContents, emptyMap(), ::report, null).get()?.classpath?.toList()
        } catch (e: Throwable) {
            return ResultWithDiagnostics.Failure(
                *diagnostics.toTypedArray(),
                e.asDiagnostics(path = context.script.locationId)
            )
        }

        return ScriptCompilationConfiguration(context.compilationConfiguration) {
            println("resolved classpath: (${resolvedClassPath?.size ?: 0}) $resolvedClassPath")
            if (resolvedClassPath != null) updateClasspath(resolvedClassPath)
            if (importedSources.isNotEmpty()) {
                importScripts.append(importedSources)
                val v = importedSources
                    .map { it.file.name }
                    .map {
                        it.replace(".plugin.conf", "_plugin_conf")
                            .replace(".kts", ".*")
                    }
                defaultImports.append(v)
            }
        }.asSuccess(diagnostics)
    }
}

