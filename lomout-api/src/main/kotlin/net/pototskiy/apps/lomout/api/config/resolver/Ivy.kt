package net.pototskiy.apps.lomout.api.config.resolver

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

class IvyResolver : GenericRepositoryWithBridge {
    private val logger = MainAndIdeLogger()

    private fun String?.isValidParam() = this?.isNotBlank() ?: false

    override fun tryResolve(artifactCoordinates: GenericArtifactCoordinates): Iterable<File>? =
        tryResolve(artifactCoordinates, emptyList())

    fun tryResolve(
        artifactCoordinates: GenericArtifactCoordinates,
        excludes: List<Pair<String, String>> = emptyList()
    ): Iterable<File>? =
        with(artifactCoordinates) {
            val artifactId = artifactId()
            logger.trace("Try to resolve artifact: $artifactId")
            val artifact = resolveArtifact(artifactId, excludes)
            if (artifact.isEmpty()) {
                logger.error("Can not resolve artifact: artifactId")
            } else {
                logger.trace(
                    "Artifact $artifactId is resolved to files: ${artifact.joinToString(",") { it.absolutePath }}"
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
                error("Unknown set of arguments to maven resolver: $stringCoordinates")
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

    companion object {
        const val DEFAULT_ARTIFACT_PATTERN = "[organisation]/[module]/[revision]/[artifact](-[revision]).[ext]"

        init {
            Message.setDefaultLogger(DefaultMessageLogger(1))
        }
    }
}

class FilesAndIvyResolver :
    KotlinAnnotatedScriptDependenciesResolver(
        emptyList(),
        arrayListOf(DirectResolver(), IvyResolver()).asIterable()
    )

fun mavenCentral() = BasicRepositoryCoordinates(
    "https://repo.maven.apache.org/maven2/",
    "maven:mavenCentral"
)

fun jCenter() = BasicRepositoryCoordinates(
    "https://jcenter.bintray.com/",
    "bintray:jcenter"
)
