@file:Suppress("unused")

package net.pototskiy.apps.magemediation.api.config

import org.jetbrains.kotlin.script.util.DependsOn
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
    displayName = "Magento mediation plugin script",
    fileExtension = ".*\\.plugin\\.kts",
    compilationConfiguration = PluginScriptCompilationOptions::class
)
abstract class PluginScript(@Suppress("UNUSED_PARAMETER") args: Array<String>)

object PluginScriptCompilationOptions : ScriptCompilationConfiguration({
    displayName("Magento mediation plugin script")
    fileExtension("plugin.kts")
    baseClass(PluginScript::class)
    defaultImports(
        "org.jetbrains.kotlin.script.util.*",
        "net.pototskiy.apps.magemediation.api.database.*",
        "net.pototskiy.apps.magemediation.api.config.*",
        "net.pototskiy.apps.magemediation.api.config.mediator.*",
        "net.pototskiy.apps.magemediation.api.entity.*",
        "net.pototskiy.apps.magemediation.api.source.*",
        "net.pototskiy.apps.magemediation.api.plugable.*"
    )
    compilerOptions("-jvm-target", "1.8")
    jvm {
        dependenciesFromClassloader(classLoader = this::class.java.classLoader, wholeClasspath = true)
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
        onAnnotations(DependsOn::class, handler = ConfigKtsConfigurator())
    }
})

private fun debug(suffix: String, block: PrintStream.() -> Unit) {
    val stream = PrintStream(FileOutputStream("c:/temp/plugin-script-$suffix.log", true))
    stream.use {
        it.println(Date().toString())
        it.apply(block)
    }
}
