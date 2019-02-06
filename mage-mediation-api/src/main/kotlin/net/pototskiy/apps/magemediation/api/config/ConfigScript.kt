@file:Suppress("unused")

package net.pototskiy.apps.magemediation.api.config

import org.jetbrains.kotlin.script.util.DependsOn
import org.jetbrains.kotlin.script.util.Import
import org.jetbrains.kotlin.script.util.Repository
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.*
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
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
    fun isClassInPath(name: String): Boolean {
        return try {
            Class.forName(name, false, ConfigScriptCompilationConfiguration::class.java.classLoader)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    fun checkAndGetExternalDeps(): List<File> {
        val resolver = GradleCacheResolver()
        val deps = mutableListOf<File>()
        if (!isClassInPath("org.jetbrains.kotlin.script.util.Import")) {
            deps.addAll(resolver.tryResolve("org.jetbrains:kotlin-script-util:1.3.20") ?: emptyList())
        }
        return deps
    }

    displayName("Magento mediation config script")
    fileExtension("conf.kts")
    baseClass(ConfigScript::class)
    defaultImports(DependsOn::class, Repository::class, Import::class)
    defaultImports(
        "org.jetbrains.kotlin.script.util.*",
        "net.pototskiy.apps.magemediation.api.config.*"
    )
    jvm {
        dependenciesFromClassloader(classLoader = this::class.java.classLoader, wholeClasspath = true)
        updateClasspath(checkAndGetExternalDeps())
        updateClasspath(
            listOf(
                File("E:/home/alexander/Development/Web/oooast-tools/mage-mediation-api/build/classes/kotlin/main")
            )
        )
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
    refineConfiguration {
        onAnnotations(DependsOn::class, Import::class, handler = ConfigKtsConfigurator())
    }
})


private fun debug(suffix: String, block: PrintStream.() -> Unit) {
    val stream = PrintStream(FileOutputStream("c:/temp/config-script-$suffix.log", true))
    stream.use {
        it.println(Date().toString())
        it.apply(block)
    }
}

