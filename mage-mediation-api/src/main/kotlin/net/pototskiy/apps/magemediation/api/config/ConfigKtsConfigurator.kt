package net.pototskiy.apps.magemediation.api.config

import org.jetbrains.kotlin.script.util.DependsOn
import org.jetbrains.kotlin.script.util.Import
import org.jetbrains.kotlin.script.util.KotlinAnnotatedScriptDependenciesResolver
import org.jetbrains.kotlin.script.util.Repository
import org.jetbrains.kotlin.script.util.resolvers.DirectResolver
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.*
import kotlin.script.dependencies.ScriptContents
import kotlin.script.dependencies.ScriptDependenciesResolver
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvm.JvmDependency
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
        debug("stack") {
            Thread.currentThread().stackTrace.forEach {
                println("${it.methodName}:${it.className}:${it.fileName}:${it.lineNumber}")
            }
        }
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
            if (resolvedClassPath != null) updateClasspath(resolvedClassPath)
            if (importedSources.isNotEmpty()) importScripts.append(importedSources)
        }.also { printConfiguration(it) }.asSuccess(diagnostics)
    }
}

private fun debug(suffix: String, block: PrintStream.() -> Unit) {
    val stream = PrintStream(FileOutputStream("c:/temp/${ConfigKtsConfigurator::class.simpleName}-$suffix.log", true))
    stream.use {
        it.println(Date().toString())
        it.apply(block)
    }
}

private fun printConfiguration(config: ScriptCompilationConfiguration) {
    debug("new-api") {
        println("displayName: ${config[ScriptCompilationConfiguration.displayName]}")
        println("fileExtension: ${config[ScriptCompilationConfiguration.fileExtension]}")
        println("baseClass: ${config[ScriptCompilationConfiguration.baseClass]}")
        println("implicitReceivers: ${config[ScriptCompilationConfiguration.implicitReceivers]?.joinToString(",\n\t") { it.typeName }
            ?: ""}")
        println(
            "providedProperties: ${config[ScriptCompilationConfiguration.providedProperties]?.entries?.joinToString(
                ",\n"
            ) { "${it.key}:${it.value.typeName}" }
                ?: ""}")
        println("defaultImports: ${config[ScriptCompilationConfiguration.defaultImports]?.joinToString(",\n\t") ?: ""}")
        println("importScripts: ${config[ScriptCompilationConfiguration.importScripts]?.joinToString(",\n\t") {
            (it as FileScriptSource).file.absolutePath ?: ""
        }
            ?: ""}")
        println("dependencies: ${config[ScriptCompilationConfiguration.dependencies]
            ?.joinToString(",\n\t") { dep ->
                (dep as? JvmDependency)?.classpath?.joinToString(
                    ",\n\t"
                ) { it.absolutePath ?: "" } ?: ""
            } ?: ""}")
        println(
            "compilerOptions: ${config[ScriptCompilationConfiguration.compilerOptions]?.joinToString(",\n\t") ?: ""}"
        )
        println("sourceFragments: ${config[ScriptCompilationConfiguration.sourceFragments]?.joinToString(",\n\t")}")
    }
}


