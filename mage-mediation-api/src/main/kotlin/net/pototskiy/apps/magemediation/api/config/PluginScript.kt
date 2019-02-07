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
//    refineConfiguration {
//        beforeCompiling { ctx ->
//            val prevDeps = (ctx.compilationConfiguration[ScriptCompilationConfiguration.dependencies]
//                ?: emptyList())
//                .mapNotNull { it as? JvmDependency }
//                .map { it.classpath }
//                .flatten()
//            val scriptLibs = ((ctx.script as? FileScriptSource)?.file?.let { file ->
//                File(file.parentFile, "scriptLibs")
//                    .listFiles(FileFilter { it.extension == "jar" }).toList()
//                    .filter { candidate -> !prevDeps.any { it.isFile && it.name == candidate.name } }
//            } ?: emptyList())
//            if (scriptLibs.isNotEmpty()) {
//                ScriptCompilationConfiguration(ctx.compilationConfiguration) {
//                    updateClasspath(scriptLibs)
//                }.asSuccess()
//            } else {
//                ctx.compilationConfiguration.asSuccess()
//            }
//        }
//    }
})

private fun debug(suffix: String, block: PrintStream.() -> Unit) {
    val stream = PrintStream(FileOutputStream("c:/temp/plugin-script-$suffix.log", true))
    stream.use {
        it.println(Date().toString())
        it.apply(block)
    }
}
