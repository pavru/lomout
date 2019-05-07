@file:Suppress("unused")

package net.pototskiy.apps.lomout.api.config

import net.pototskiy.apps.lomout.api.BuildInfo
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
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath

@Suppress("DEPRECATION")
@KotlinScript(
    displayName = "LoMout config script",
    fileExtension = ".*\\.conf\\.kts",
    compilationConfiguration = ConfigScriptCompilationConfiguration::class
)
abstract class ConfigScript(val args: Array<String>) {
    var evaluatedConfig: Config? = null

    companion object {
        var ivyFile: File? = null
    }
}

object ConfigScriptCompilationConfiguration : ScriptCompilationConfiguration({
    displayName("LoMout config script")
    fileExtension("conf.kts")
    baseClass(ConfigScript::class)
    defaultImports(
        "org.jetbrains.kotlin.script.util.*",
        "net.pototskiy.apps.lomout.api.*",
        "net.pototskiy.apps.lomout.api.database.*",
        "net.pototskiy.apps.lomout.api.config.*",
        "net.pototskiy.apps.lomout.api.config.mediator.*",
        "net.pototskiy.apps.lomout.api.plugable.*",
        "net.pototskiy.apps.lomout.api.entity.*",
        "net.pototskiy.apps.lomout.api.entity.values.*",
        "net.pototskiy.apps.lomout.api.entity.reader.*",
        "net.pototskiy.apps.lomout.api.entity.writer.*",
        "net.pototskiy.apps.lomout.api.source.*",
        "net.pototskiy.apps.lomout.api.source.workbook.*"
    )
    compilerOptions(
        "-jvm-target", "1.8",
        "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
        "-Xuse-experimental=kotlin.Experimental"
    )
    jvm {
        val logger = MainAndIdeLogger()
        dependenciesFromClassloader(
            "lomout-api",
            classLoader = ConfigScriptCompilationConfiguration::class.java.classLoader,
            wholeClasspath = false
        )
        checkAndGetExternalDeps(ConfigScriptCompilationConfiguration::class.java.classLoader)
            .takeIf { it.isNotEmpty() }?.let { updateClasspath(it) }
        this[dependencies]?.forEach { dependency ->
            logger.trace("Script classpath (final): ${(dependency as JvmDependency).classpath.joinToString(",") { it.absolutePath }}")
        }
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
    refineConfiguration {
        onAnnotations(DependsOn::class, Repository::class, Import::class, handler = KtsConfigurator())
    }
})

private fun isClassInPath(name: String, classLoader: ClassLoader): Boolean {
    return try {
        Class.forName(name, false, classLoader)
        true
    } catch (e: ClassNotFoundException) {
        false
    }
}

private fun checkAndGetExternalDeps(classLoader: ClassLoader): List<File> {
    val logger = MainAndIdeLogger()
    logger.trace("Try to check and add external dependency")
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
    ConfigScript.ivyFile?.let { deps.addAll(resolver.tryResolveExternalDependency(it)) }

    return deps
}
