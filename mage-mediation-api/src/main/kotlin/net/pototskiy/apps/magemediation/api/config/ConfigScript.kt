@file:Suppress("unused")

package net.pototskiy.apps.magemediation.api.config

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
    displayName = "Magento mediation config script",
    fileExtension = ".*\\.conf\\.kts",
    compilationConfiguration = ConfigScriptCompilationConfiguration::class
)
abstract class ConfigScript(val args: Array<String>) {
    var evaluatedConfig: Config? = null
}

object ConfigScriptCompilationConfiguration : ScriptCompilationConfiguration({
    displayName("Magento mediation config script")
    fileExtension("conf.kts")
    baseClass(ConfigScript::class)
    defaultImports(DependsOn::class, Repository::class, Import::class)
    defaultImports(
        "org.jetbrains.kotlin.script.util.*",
        "net.pototskiy.apps.magemediation.api.*",
        "net.pototskiy.apps.magemediation.api.database.*",
        "net.pototskiy.apps.magemediation.api.config.*",
        "net.pototskiy.apps.magemediation.api.config.mediator.*",
        "net.pototskiy.apps.magemediation.api.plugable.*",
        "net.pototskiy.apps.magemediation.api.entity.*",
        "net.pototskiy.apps.magemediation.api.entity.values.*",
        "net.pototskiy.apps.magemediation.api.entity.reader.*",
        "net.pototskiy.apps.magemediation.api.entity.writer.*",
        "net.pototskiy.apps.magemediation.api.source.*",
        "net.pototskiy.apps.magemediation.api.source.workbook.*"
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
    if (!isClassInPath("org.jetbrains.kotlin.script.util.Import", classLoader)) {
        resolver.tryAddRepository(mavenCentral())
        deps.addAll(resolver.tryResolve("org.jetbrains:kotlin-script-util:1.3.21")
            ?: emptyList())
    }
    if (!isClassInPath("net.pototskiy.apps.magemediation.api.config.Config", classLoader)) {
        resolver.tryAddRepository(localMaven())
        deps.addAll(resolver.tryResolve("oooast-tools:mage-mediation-api:latest.integration")
            ?: emptyList())
    }
    return deps
}
