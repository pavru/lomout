@file:Suppress("unused")

package net.pototskiy.apps.lomout.api.config

import net.pototskiy.apps.lomout.api.config.resolver.IvyResolver
import net.pototskiy.apps.lomout.api.config.resolver.mavenCentral
import org.jetbrains.kotlin.script.util.DependsOn
import org.jetbrains.kotlin.script.util.Import
import org.jetbrains.kotlin.script.util.Repository
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptAcceptedLocation
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.acceptedLocations
import kotlin.script.experimental.api.baseClass
import kotlin.script.experimental.api.compilerOptions
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.displayName
import kotlin.script.experimental.api.fileExtension
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.api.refineConfiguration
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
    defaultImports(DependsOn::class, Repository::class, Import::class)
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
        dependenciesFromClassloader(classLoader = this::class.java.classLoader, wholeClasspath = true)
        checkAndGetExternalDeps(ConfigScriptCompilationConfiguration::class.java.classLoader)
            .takeIf { it.isNotEmpty() }?.let { updateClasspath(it) }
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
    val resolver = IvyResolver()
    val deps = mutableListOf<File>()
    fun testAndAdd(klass: String, artifacts: List<String>) {
        if (!isClassInPath(klass, classLoader)) {
            resolver.tryAddRepository(mavenCentral())
            artifacts.forEach {
                deps.addAll(resolver.tryResolve(it) ?: emptyList())
            }
        }
    }
    testAndAdd(
        "org.jetbrains.kotlin.script.util.Import",
        listOf("org.jetbrains:kotlin-script-util:1.3.31")
    )
    testAndAdd(
        "net.pototskiy.apps.lomout.api.config.Config",
        listOf("lomout:lomout-api:1.1.5")
    )
    ConfigScript.ivyFile
        ?.let { deps.addAll(resolver.tryResolveExternalDependency(it)) }

    return deps
}
