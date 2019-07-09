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

package net.pototskiy.apps.lomout.api.config.resolver

import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.config.MainAndIdeLogger
import org.apache.ivy.Ivy
import org.apache.ivy.core.LogOptions
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor
import org.apache.ivy.core.module.descriptor.DefaultExcludeRule
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
import org.apache.ivy.core.module.id.ArtifactId
import org.apache.ivy.core.module.id.ModuleId
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.resolve.ResolveOptions
import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.plugins.matcher.ExactOrRegexpPatternMatcher
import org.apache.ivy.plugins.matcher.PatternMatcher
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorWriter
import org.apache.ivy.plugins.resolver.BintrayResolver
import org.apache.ivy.plugins.resolver.ChainResolver
import org.apache.ivy.plugins.resolver.IBiblioResolver
import org.apache.ivy.plugins.resolver.URLResolver
import org.apache.ivy.plugins.version.MavenTimedSnapshotVersionMatcher
import org.apache.ivy.util.DefaultMessageLogger
import org.apache.ivy.util.Message
import org.jetbrains.kotlin.script.util.KotlinAnnotatedScriptDependenciesResolver
import org.jetbrains.kotlin.script.util.resolvers.DirectResolver
import org.jetbrains.kotlin.script.util.resolvers.experimental.BasicRepositoryCoordinates
import org.jetbrains.kotlin.script.util.resolvers.experimental.GenericArtifactCoordinates
import org.jetbrains.kotlin.script.util.resolvers.experimental.GenericRepositoryCoordinates
import org.jetbrains.kotlin.script.util.resolvers.experimental.GenericRepositoryWithBridge
import org.jetbrains.kotlin.script.util.resolvers.experimental.MavenArtifactCoordinates
import java.io.File

/**
 * Ivy dependency resolver
 *
 * @property logger MainAndIdeLogger
 * @property ivyResolvers ArrayList<URLResolver>
 */
class IvyResolver : GenericRepositoryWithBridge {
    private val logger = MainAndIdeLogger()

    private fun String?.isValidParam() = this?.isNotBlank() ?: false

    /**
     * Resolve artifact
     *
     * @param artifactCoordinates GenericArtifactCoordinates The artifact coordinates
     * @return Iterable<File>? Artifact files or null if cannot resolve
     */
    override fun tryResolve(artifactCoordinates: GenericArtifactCoordinates): Iterable<File>? =
        tryResolve(artifactCoordinates, emptyList())

    /**
     * Resolve artifact with exclude some artifacts
     *
     * @param artifactCoordinates GenericArtifactCoordinates The artifact coordinates
     * @param excludes List<Pair<String, String>> List of artifact to exclude Pair(Group, Name)
     * @return Iterable<File>? Artifact files or null if cannot resolve
     */
    fun tryResolve(
        artifactCoordinates: GenericArtifactCoordinates,
        excludes: List<Pair<String, String>> = emptyList()
    ): Iterable<File>? =
        with(artifactCoordinates) {
            val artifactId = artifactId()
            logger.trace(message("message.trace.config.resolver.try_to_resolve", artifactId))
            val artifact = resolveArtifact(artifactId, excludes)
            if (artifact.isEmpty()) {
                logger.error(message("message.error.config.resolver.cannot_resolve", artifactId))
            } else {
                logger.trace(
                    message(
                        "message.trace.config.resolver.resolved",
                        artifactId,
                        artifact.joinToString(",") { it.absolutePath })
                )
            }
            if (artifact.isEmpty()) null else artifact
        }

    private fun GenericArtifactCoordinates.artifactId(): List<String> {
        return if (this is MavenArtifactCoordinates && (groupId.isValidParam() || artifactId.isValidParam())) {
            listOf(groupId.orEmpty(), artifactId.orEmpty(), version.orEmpty())
        } else {
            val stringCoordinates = string
            if (stringCoordinates.isValidParam() && stringCoordinates.count { it == ':' } == 2) {
                stringCoordinates.split(':')
            } else {
                error(message("message.error.config.resolver.wrong_maven_args", stringCoordinates))
            }
        }
    }

    private val ivyResolvers = arrayListOf<URLResolver>()

    private fun resolveArtifact(
        artifactId: List<String>,
        excludes: List<Pair<String, String>> = emptyList()
    ): List<File> {

        val ivySettings = ivySettings()

        val ivy = Ivy.newInstance(ivySettings)

        val ivyFile = File.createTempFile("ivy", ".xml")
        ivyFile.deleteOnExit()

        val moduleDescriptor = DefaultModuleDescriptor.newDefaultInstance(
            ModuleRevisionId.newInstance(artifactId[0], artifactId[1] + "-caller", "working")
        )

        val depsDescriptor = DefaultDependencyDescriptor(
            moduleDescriptor,
            ModuleRevisionId.newInstance(
                artifactId[0],
                artifactId[1],
                artifactId[2]
            ),
            false, false, true
        )
        depsDescriptor.addDependencyConfiguration("default", "default")
        moduleDescriptor.addDependency(depsDescriptor)
        for (pair in excludes) {
            val rule = DefaultExcludeRule(
                ArtifactId(
                    ModuleId(pair.first, pair.second),
                    PatternMatcher.ANY_EXPRESSION,
                    PatternMatcher.ANY_EXPRESSION,
                    PatternMatcher.ANY_EXPRESSION
                ),
                ExactOrRegexpPatternMatcher(),
                null
            )
            depsDescriptor.addExcludeRule("default", rule)
        }

        // creates an ivy configuration file
        XmlModuleDescriptorWriter.write(moduleDescriptor, ivyFile)
        logger.trace(ivyFile.readText())

        val resolveOptions = ResolveOptions().apply {
            confs = arrayOf("default")
            log = LogOptions.LOG_QUIET
            isOutputReport = false
        }

        // init resolve report
        val report = ivy.resolve(ivyFile.toURI().toURL(), resolveOptions)

        return report.allArtifactsReports.map { it.localFile }
    }

    private fun ivySettings(): IvySettings {
        if (ivyResolvers.isEmpty() || ivyResolvers.none { it.name == "central" }) {
            ivyResolvers.add(
                URLResolver().apply {
                    isM2compatible = true
                    name = "central"
                    addArtifactPattern("https://repo1.maven.org/maven2/$DEFAULT_ARTIFACT_PATTERN")
                }
            )
        }
        return IvySettings().apply {
            val resolver =
                if (ivyResolvers.size == 1) ivyResolvers.first()
                else ChainResolver().also {
                    for (resolver in ivyResolvers) {
                        it.add(resolver)
                    }
                }
            addResolver(resolver)
            setDefaultResolver(resolver.name)
            addVersionMatcher(MavenTimedSnapshotVersionMatcher())
        }
    }

    /**
     * Resolve an external artifact defined in the ivy.xml file
     *
     * @param ivyFile File The ivy.xml file
     * @return List<File> List of artifact file
     */
    fun tryResolveExternalDependency(ivyFile: File): List<File> {
        val ivy = Ivy.newInstance(ivySettings())
        val resolveOptions = ResolveOptions().apply {
            confs = arrayOf("default")
            log = LogOptions.LOG_QUIET
            isOutputReport = false
        }
        val report = ivy.resolve(ivyFile, resolveOptions)
        return report.allArtifactsReports.map { it.localFile }
    }

    /**
     * Add a repository to resolver
     *
     * @param repositoryCoordinates GenericRepositoryCoordinates
     * @return Boolean
     */
    override fun tryAddRepository(repositoryCoordinates: GenericRepositoryCoordinates): Boolean {
        val url = repositoryCoordinates.url
        val type = repositoryCoordinates.name?.split(":")?.first()
        if (url != null) {
            return when (type) {
                "maven" -> addMavenRepository(repositoryCoordinates)
                "bintray" -> addBintrayRepository(repositoryCoordinates)
                else -> addUrlBasedRepository(repositoryCoordinates)
            }
        }
        return false
    }

    private fun addUrlBasedRepository(repositoryCoordinates: GenericRepositoryCoordinates): Boolean {
        val url = repositoryCoordinates.url
        if (url != null) {
            ivyResolvers.add(
                URLResolver().apply {
                    isM2compatible = true
                    name = repositoryCoordinates.name.takeIf { it.isValidParam() } ?: url.host
                    addArtifactPattern(
                        "${url.toString().let { if (it.endsWith('/')) it else "$it/" }}$DEFAULT_ARTIFACT_PATTERN"
                    )
                }
            )
            return true
        }
        return false
    }

    private fun addMavenRepository(repositoryCoordinates: GenericRepositoryCoordinates): Boolean {
        val url = repositoryCoordinates.url
        if (url != null) {
            ivyResolvers.add(
                IBiblioResolver().apply {
                    isM2compatible = true
                    name = repositoryCoordinates.name.takeIf { it.isValidParam() } ?: url.host
                    root = url.toString()
                    setCheckmodified(true)
                }
            )
            return true
        }
        return false
    }

    private fun addBintrayRepository(repositoryCoordinates: GenericRepositoryCoordinates): Boolean {
        val url = repositoryCoordinates.url
        if (url != null) {
            ivyResolvers.add(
                BintrayResolver().apply {
                    isM2compatible = true
                    name = repositoryCoordinates.name.takeIf { it.isValidParam() } ?: url.host
                    root = url.toString()
                    setCheckmodified(true)
                }
            )
            return true
        }
        return false
    }

    /**
     * Companion object
     */
    companion object {
        /**
         * Default artifact pattern
         */
        const val DEFAULT_ARTIFACT_PATTERN = "[organization]/[module]/[revision]/[artifact](-[revision]).[ext]"

        init {
            Message.setDefaultLogger(DefaultMessageLogger(1))
        }
    }
}

/**
 * File and Ivy dependency resolver
 */
class FilesAndIvyResolver :
    KotlinAnnotatedScriptDependenciesResolver(
        emptyList(),
        arrayListOf(DirectResolver(), IvyResolver()).asIterable()
    )

/**
 * Get maven central url
 *
 * @return BasicRepositoryCoordinates
 */
fun mavenCentral() = BasicRepositoryCoordinates(
    "https://repo.maven.apache.org/maven2/",
    "maven:mavenCentral"
)

/**
 * Get jCenter url
 *
 * @return BasicRepositoryCoordinates
 */
@Suppress("GraziInspection")
fun jCenter() = BasicRepositoryCoordinates(
    "https://jcenter.bintray.com/",
    "bintray:jcenter"
)
