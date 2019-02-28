@file:Suppress("unused")

package net.pototskiy.apps.magemediation.api.config

import org.jetbrains.kotlin.script.util.DependsOn
import org.jetbrains.kotlin.script.util.Import
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.util.*
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.streams.toList


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
//    defaultImports(DependsOn::class, Repository::class, Import::class)
    defaultImports(
        "org.jetbrains.kotlin.script.util.*"
        , "net.pototskiy.apps.magemediation.api.*"
        , "net.pototskiy.apps.magemediation.api.database.*"
        , "net.pototskiy.apps.magemediation.api.config.*"
        , "net.pototskiy.apps.magemediation.api.config.mediator.*"
        , "net.pototskiy.apps.magemediation.api.plugable.*"
        , "net.pototskiy.apps.magemediation.api.entity.*"
        , "net.pototskiy.apps.magemediation.api.entity.values.*"
        , "net.pototskiy.apps.magemediation.api.entity.reader.*"
        , "net.pototskiy.apps.magemediation.api.entity.writer.*"
        , "net.pototskiy.apps.magemediation.api.source.*"
        , "net.pototskiy.apps.magemediation.api.source.workbook.*"
    )
    compilerOptions("-jvm-target", "1.8")
    jvm {
        dependenciesFromClassloader(classLoader = this::class.java.classLoader, wholeClasspath = true)
        updateClasspath(checkAndGetExternalDeps())
        val containsApiModule = this[ScriptCompilationConfiguration.dependencies]?.any {
            it is JvmDependency &&
                    it.classpath.any { classpath ->
                        classpath.absolutePath.toLowerCase().contains("mage-mediation-api")
                    }
        } ?: false
        if (!containsApiModule) {
            updateClasspath(
                listOf(
                    File("E:/home/alexander/Development/Web/oooast-tools/mage-mediation-api/build/classes/kotlin/main")
                )
            )
        }
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
    refineConfiguration {
        beforeParsing { context ->
            val scriptBaseDir = (context.script as? FileScriptSource)?.file?.absoluteFile?.parentFile
            val files = Files.walk(scriptBaseDir?.toPath())
                .map { it.toFile() }
                .toList()
                .filter { it.isFile && it.name.endsWith(".plugin.conf.kts") }

            ScriptCompilationConfiguration(context.compilationConfiguration) {
                if (files.isNotEmpty()) {
                    defaultImports.append(files
                        .map { it.name }
                        .map {
                            it.replace(".plugin.conf", "_plugin_conf")
                                .replace(".kts", ".*")
                        })
                }
            }.asSuccess()
        }
        onAnnotations(DependsOn::class, Import::class, handler = ConfigKtsConfigurator())
    }
})

private fun isClassInPath(name: String): Boolean {
    return try {
        Class.forName(name, false, ConfigScriptCompilationConfiguration::class.java.classLoader)
        true
    } catch (e: ClassNotFoundException) {
        false
    }
}

private fun checkAndGetExternalDeps(): List<File> {
    val resolver = GradleCacheResolver()
    val deps = mutableListOf<File>()
    if (!isClassInPath("org.jetbrains.kotlin.script.util.Import")) {
        deps.addAll(resolver.tryResolve("org.jetbrains:kotlin-script-util:1.3.20") ?: emptyList())
    }
    return deps
}

private fun debug(suffix: String, block: PrintStream.() -> Unit) {
    val stream = PrintStream(FileOutputStream("c:/temp/config-script-$suffix.log", true))
    stream.use {
        it.println(Date().toString())
        it.apply(block)
    }
}


