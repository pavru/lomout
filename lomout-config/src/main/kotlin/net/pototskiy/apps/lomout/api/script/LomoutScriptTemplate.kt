/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

@file:Suppress("unused")

package net.pototskiy.apps.lomout.api.script

import net.pototskiy.apps.lomout.api.BuildInfo
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.script.resolver.IvyResolver
import net.pototskiy.apps.lomout.api.script.resolver.jCenter
import net.pototskiy.apps.lomout.api.script.resolver.localMaven
import net.pototskiy.apps.lomout.api.script.resolver.mavenCentral
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
import kotlin.script.experimental.api.filePathPattern
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.api.refineConfiguration
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.JvmScriptCompilationConfigurationBuilder
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClassloader

/**
 * LoMout script definition for Kotlin script host
 *
 * @property args The arguments
 * @property lomoutScript The configuration
 * @constructor
 */
@Suppress("DEPRECATION")
@KotlinScript(
    displayName = "LoMout script",
    fileExtension = """.*\.lomout\.kts""",
    compilationConfiguration = LomoutScriptCompilationConfiguration::class
)
abstract class LomoutScriptTemplate(private val args: Array<String>) {
    var lomoutScript: LomoutScript? = null
}

/**
 * LoMout script compilation configuration
 */
object LomoutScriptCompilationConfiguration : ScriptCompilationConfiguration({
    displayName("LoMout script")
    fileExtension("lomout.kts")
    filePathPattern(""".*\.lomout\.kts""")
    baseClass(LomoutScriptTemplate::class)
    defaultImports(Import::class, DependsOn::class, Repository::class)
    defaultImports(
        "org.jetbrains.kotlin.script.util.*",
        "java.time.LocalDate",
        "java.time.LocalDateTime",
        "net.pototskiy.apps.lomout.api.*",
        "net.pototskiy.apps.lomout.api.script.*",
        "net.pototskiy.apps.lomout.api.script.script",
        "net.pototskiy.apps.lomout.api.script.loader.*",
        "net.pototskiy.apps.lomout.api.script.mediator.*",
        "net.pototskiy.apps.lomout.api.script.pipeline.*",
        "net.pototskiy.apps.lomout.api.script.printer.*",
        "net.pototskiy.apps.lomout.api.callable.*",
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
        val libs = if (!addApiToClasspath()) {
            arrayOf("kmongo-property", "log4j-api", "bson", "kotlin-script-util")
        } else {
            emptyArray()
        }
        @Suppress("SpreadOperator")
        dependenciesFromClassloader(
            *libs,
            classLoader = LomoutScriptCompilationConfiguration::class.java.classLoader,
            wholeClasspath = false
        )
        updateClasspath(dependenciesFromBuildInfo())
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
    val classpath = classpathFromClassloader(LomoutScriptCompilationConfiguration::class.java.classLoader)
    val apiInPlace = classpath?.firstOrNull {
        it.isDirectory &&
                it.absolutePath.contains(Regex("""${BuildInfo.moduleName}.build.classes.kotlin.main$"""))
    }
    val apiInJar = classpath?.firstOrNull {
        @Suppress("GraziInspection")
        it.isFile && it.name.contains(Regex("""${BuildInfo.moduleName}.*\.jar$"""))
    }
    if (apiInPlace != null && apiInJar == null) {
        updateClasspath(listOf(apiInPlace))
    } else if (apiInJar != null) {
        updateClasspath(listOf(apiInJar))
    }
    return apiInJar != null
}

private fun dependenciesFromBuildInfo(): List<File> {
    val logger = MainAndIdeLogger()
    logger.trace(message("message.trace.script.buildinfo.dependency.check"))
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
    logger.trace(message("message.trace.script.ivy.dependency.check"))
    val resolver = IvyResolver()
    resolver.tryAddRepository(mavenCentral())
    resolver.tryAddRepository(jCenter())
    resolver.tryAddRepository(localMaven())
    val deps = mutableListOf<File>()

    deps.addAll(resolver.tryResolveExternalDependency(ivyFile))

    return deps
}
