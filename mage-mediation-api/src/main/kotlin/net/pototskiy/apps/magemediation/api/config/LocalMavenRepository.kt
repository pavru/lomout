package net.pototskiy.apps.magemediation.api.config

import java.io.File
import javax.xml.stream.XMLInputFactory

class LocalMavenRepository {

    fun findLocalMavenRepo(): File? {
        val (m2Home, m2Conf) = getMavenLocalHome()
        if (!m2Conf.exists()) return null
        val settings = m2Conf.resolve("settings.xml")
        val repoLocation = if (settings.exists()) readLocationFromSettings(settings) else null
        return when {
            repoLocation != null && File(repoLocation).exists() -> File(repoLocation)
            m2Home.resolve("repository").exists() -> m2Home.resolve("repository")
            else -> null
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
        val envSetting = System.getenv("M2_HOME")
        return if (envSetting != null && File(envSetting).exists()) {
            listOf(File(envSetting), File(envSetting).resolve("conf"))
        } else {
            val m2Location = File(System.getProperty("user.home")).resolve(".m2")
            listOf(m2Location, m2Location)
        }
    }
}

fun localMaven() = LocalMavenRepository().findLocalMavenRepo()?.toURI()?.toURL()?.toString() ?: ""
