package net.pototskiy.apps.magemediation.api.config

import org.jetbrains.kotlin.script.util.resolvers.experimental.GenericArtifactCoordinates
import org.jetbrains.kotlin.script.util.resolvers.experimental.GenericRepositoryCoordinates
import org.jetbrains.kotlin.script.util.resolvers.experimental.GenericRepositoryWithBridge
import java.io.File

class GradleCacheResolver : GenericRepositoryWithBridge {
    override fun tryAddRepository(repositoryCoordinates: GenericRepositoryCoordinates): Boolean = false

    override fun tryResolve(artifactCoordinates: GenericArtifactCoordinates): Iterable<File>? {
        val repo = findGradleSharedCache() ?: return emptyList()
        val (group, artifact, version) = artifactCoordinates.string.split(":")
        val jarCache = repo
            .resolve(group)
            .resolve(artifact)
            .resolve(version)
        val jar = jarCache.listFiles()
            .mapNotNull { dir ->
                if (dir.resolve("$artifact-$version.jar").exists()) {
                    dir.resolve("$artifact-$version.jar")
                } else {
                    null
                }
            }
        return jar
    }

    private fun findGradleSharedCache(): File? {
        val gradleHome = when {
            System.getenv("GRADLE_USER_HOME") != null -> File(System.getenv("GRADLE_USER_HOME"))
            else -> File(System.getProperty("user.home")).resolve(".gradle")
        }
        if (!gradleHome.exists()) return null
        val cache = gradleHome.resolve("caches/modules-2/files-2.1")
        return if (cache.exists()) cache else null
    }
}
