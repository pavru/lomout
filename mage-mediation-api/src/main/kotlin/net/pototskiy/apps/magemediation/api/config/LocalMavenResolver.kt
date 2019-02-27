package net.pototskiy.apps.magemediation.api.config

import org.jetbrains.kotlin.script.util.resolvers.experimental.GenericArtifactCoordinates
import org.jetbrains.kotlin.script.util.resolvers.experimental.GenericRepositoryCoordinates
import org.jetbrains.kotlin.script.util.resolvers.experimental.GenericRepositoryWithBridge
import java.io.File
import javax.xml.stream.XMLInputFactory

class LocalMavenResolver : GenericRepositoryWithBridge {
    override fun tryAddRepository(repositoryCoordinates: GenericRepositoryCoordinates): Boolean = false

    override fun tryResolve(artifactCoordinates: GenericArtifactCoordinates): Iterable<File>? {
        val repo = findLocalMavenRepo() ?: return emptyList()
        val (group, artifact, version) = artifactCoordinates.string.split(":")
        val jar = repo
            .resolve(group.replace(".", "/"))
            .resolve(artifact)
            .resolve(version)
            .resolve("$artifact-$version.jar")
        return if (jar.exists()) listOf(jar) else emptyList()
    }

    private fun findLocalMavenRepo(): File? {
        val (m2Home, m2Conf) = getMavenLocalHome()
        if (!m2Conf.exists()) return null
        val settings = m2Conf.resolve("settings.xml")
        return if (settings.exists()) {
            val repoLocation = readLocationFromSettings(settings)
            when {
                repoLocation != null && File(repoLocation).exists() -> File(repoLocation)
                m2Home.resolve("repository").exists() -> m2Home.resolve("repository")
                else -> null
            }
        } else {
            null
        }

    }

    private fun readLocationFromSettings(settings: File): String? {
        return settings.reader().use {
            val xmlReader = XMLInputFactory.newFactory().createXMLEventReader(it)
            while (xmlReader.hasNext()) {
                val event = xmlReader.nextEvent()
                if (event.isStartElement && event.asStartElement().name.localPart == "localRepository") {
                    return@use xmlReader.nextEvent().asCharacters().data
                }
            }
            return@use null
        }
    }

    private fun getMavenLocalHome(): List<File> {
        return System.getenv("M2_HOME")?.let {
            if (File(it).exists())
                listOf(File(it), File(it).resolve("conf"))
            else {
                val m2Location = File(System.getProperty("user.home")).resolve(".m2")
                listOf(m2Location, m2Location)
            }
        } ?: emptyList()
    }
}
