@file:Suppress("unused")

package net.pototskiy.apps.lomout.api.config

import net.pototskiy.apps.lomout.api.BuildInfo
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.config.resolver.IvyResolver
import net.pototskiy.apps.lomout.api.config.resolver.jCenter
import net.pototskiy.apps.lomout.api.config.resolver.localMaven
import net.pototskiy.apps.lomout.api.config.resolver.mavenCentral
import org.jetbrains.kotlin.script.util.DependsOn
import org.jetbrains.kotlin.script.util.Import
import org.jetbrains.kotlin.script.util.Repository
import org.jetbrains.kotlin.script.util.resolvers.experimental.BasicArtifactCoordinates
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptAcceptedLocation
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.acceptedLocations
import kotlin.script.experimental.api.baseClass
import kotlin.script.experimental.api.compilerOptions
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.dependencies
import kotlin.script.experimental.api.displayName
import kotlin.script.experimental.api.fileExtension
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.api.refineConfiguration
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.JvmScriptCompilationConfigurationBuilder
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClassloader

/**
 * Config script definition for Kotlin script host
 *
 * @property args The arguments
 * @property evaluatedConfig The configuration
 * @constructor
 */
@Suppress("DEPRECATION")
@KotlinScript(
    displayName = "LoMout config script",
    fileExtension = ".*\\.conf\\.kts",
    compilationConfiguration = ConfigScriptCompilationConfiguration::class
)
abstract class ConfigScript(private val args: Array<String>) {
    var evaluatedConfig: Config? = null
}

/**
 * Config script compilation configuration
 */
object ConfigScriptCompilationConfiguration : ScriptCompilationConfiguration({
    displayName("LoMout config script")
    fileExtension("conf.kts")
    baseClass(ConfigScript::class)
    defaultImports(Import::class, DependsOn::class, Repository::class)
    defaultImports(
        "org.jetbrains.kotlin.script.util.*",
        "java.time.LocalDate",
        "java.time.LocalDateTime",
        "net.pototskiy.apps.lomout.api.*",
        "net.pototskiy.apps.lomout.api.config.*",
        "net.pototskiy.apps.lomout.api.config.config",
        "net.pototskiy.apps.lomout.api.config.loader.*",
        "net.pototskiy.apps.lomout.api.config.mediator.*",
        "net.pototskiy.apps.lomout.api.config.pipeline.*",
        "net.pototskiy.apps.lomout.api.config.printer.*",
        "net.pototskiy.apps.lomout.api.plugable.*",
        "net.pototskiy.apps.lomout.api.entity.*",
        "net.pototskiy.apps.lomout.api.entity.reader.*",
        "net.pototskiy.apps.lomout.api.entity.type.*",
        "net.pototskiy.apps.lomout.api.entity.values.*",
        "net.pototskiy.apps.lomout.api.entity.writer.*",
        "net.pototskiy.apps.lomout.api.source.*",
        "net.pototskiy.apps.lomout.api.source.workbook.*",
        "net.pototskiy.apps.lomout.api.document.*",
        "net.pototskiy.apps.lomout.api.document.DocumentMetadata.*",
        "net.pototskiy.apps.lomout.api.document.SupportAttributeType.*"
    )
    compilerOptions(
        "-jvm-target", "1.8",
        "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
        "-Xuse-experimental=kotlin.Experimental"
    )
    jvm {
        val logger = MainAndIdeLogger()
        val libs = if (addApiToClasspath()) {
            arrayOf("lomout-api")
        } else {
            arrayOf("lomout-api", "kmongo-property", "log4j-api", "bson", "kotlin-script-util")
        }
        @Suppress("SpreadOperator")
        dependenciesFromClassloader(
            *libs,
            classLoader = ConfigScriptCompilationConfiguration::class.java.classLoader,
            wholeClasspath = false
        )
        dependenciesFromBuildInfo()
            .takeIf { it.isNotEmpty() }?.let { updateClasspath(it) }
        this[dependencies]?.forEach { dependency ->
            logger.trace("Script classpath (final): " +
                    (dependency as JvmDependency)
                        .classpath.joinToString(",") { it.absolutePath }
            )
        }
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
    refineConfiguration {
        onAnnotations(DependsOn::class, Repository::class, Import::class, handler = KtsConfigurator())
    }
})

/**
 * Add lomout api in classpath from a directory.
 *
 * @receiver JvmScriptCompilationConfigurationBuilder
 * @return True if the api in the jar file
 */
private fun JvmScriptCompilationConfigurationBuilder.addApiToClasspath(): Boolean {
    val classpath = classpathFromClassloader(ConfigScriptCompilationConfiguration::class.java.classLoader)
    val apiInPlace = classpath?.firstOrNull {
        it.isDirectory &&
                it.absolutePath.contains(Regex("""lomout-api.build.classes.kotlin.main$"""))
    }
    val apiInJar = classpath?.firstOrNull {
        @Suppress("GraziInspection")
        it.isFile && it.name.contains(Regex("""lomout-api.*\.jar$"""))
    }
    if (apiInPlace != null && apiInJar == null) {
        updateClasspath(listOf(apiInPlace))
    }
    return apiInJar != null
}

private fun dependenciesFromBuildInfo(): List<File> {
    val logger = MainAndIdeLogger()
    logger.trace(message("message.trace.config.buildinfo.dependency.check"))
    val resolver = IvyResolver()
    resolver.tryAddRepository(mavenCentral())
    resolver.tryAddRepository(jCenter())
    resolver.tryAddRepository(localMaven())
    val deps = mutableListOf<File>()

    BuildInfo.dependencies.forEach { dep ->
        val resolvedArtifacts = resolver.tryResolve(
            BasicArtifactCoordinates("${dep.group}:${dep.name}:${dep.version}"),
            dep.excludeRules.map { Pair(it.group, it.name) }
        )?.toList()
        deps.addAll(resolvedArtifacts ?: emptyList())
    }

    return deps
}

/**
 * Create dependency from an ivy.xml file.
 *
 * @param ivyFile The ivy file
 * @return List<File>
 */
fun dependenciesFromIvyFile(ivyFile: File): List<File> {
    val logger = MainAndIdeLogger()
    logger.trace(message("message.trace.config.ivy.dependency.check"))
    val resolver = IvyResolver()
    resolver.tryAddRepository(mavenCentral())
    resolver.tryAddRepository(jCenter())
    resolver.tryAddRepository(localMaven())
    val deps = mutableListOf<File>()

    deps.addAll(resolver.tryResolveExternalDependency(ivyFile))

    return deps
}
