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

import org.jetbrains.kotlin.script.util.resolvers.experimental.BasicRepositoryCoordinates
import java.io.File
import javax.xml.stream.XMLInputFactory

/**
 * Local maven repository
 */
object LocalMavenRepository {

    /**
     * Find local maven repository
     *
     * @return File?
     */
    fun findLocalMavenRepo(): File? {
        val (m2Home, m2Conf) = getMavenLocalHome()
        if (!m2Conf.exists()) return null
        val settings = m2Conf.resolve("settings.xml")
        val repoLocation = if (settings.exists()) readLocationFromSettings(
            settings
        ) else null
        return when {
            repoLocation != null && File(repoLocation).exists() -> File(repoLocation)
            m2Home.resolve("repository").exists() -> m2Home.resolve("repository")
            else -> null
        }
    }

    private fun readLocationFromSettings(settings: File): String? {
        return settings.reader().use {
            val xmlReader = XMLInputFactory.newFactory().createXMLEventReader(it)
            var location: String? = null
            while (xmlReader.hasNext()) {
                val event = xmlReader.nextEvent()
                if (event.isStartElement && event.asStartElement().name.localPart == "localRepository") {
                    location = xmlReader.nextEvent().asCharacters().data
                    break
                }
            }
            location
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

/**
 * Get local maven repository url
 *
 * @return BasicRepositoryCoordinates
 */
fun localMaven() = BasicRepositoryCoordinates(
    LocalMavenRepository.findLocalMavenRepo()?.toURI()?.toURL()?.toString() ?: "",
    "maven:localMaven"
)
