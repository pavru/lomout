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

package net.pototskiy.apps.lomout.api.config

import java.io.File
import java.io.Serializable
import java.net.URL
import kotlin.script.experimental.api.ExternalSourceCode

/**
 * Script source
 *
 * @property file The script file
 * @property preloadedText The script preloaded text
 * @property externalLocation The script location
 * @property text The script text
 * @property name The script file name
 * @property locationId The script location (path)
 * @property textSafe The script text
 * @constructor
 */
class SerializableFileScriptSource(
    val file: File,
    private val preloadedText: String? = null
) : ExternalSourceCode, Serializable {
    override val externalLocation: URL get() = file.toURI().toURL()
    override val text: String by lazy { preloadedText ?: file.readText() }
    override val name: String? get() = file.name
    override val locationId: String? get() = file.path

    /**
     * Is equal
     *
     * @param other
     * @return
     */
    override fun equals(other: Any?): Boolean =
        this === other ||
                (other as? SerializableFileScriptSource)?.let {
                    file.absolutePath == it.file.absolutePath && textSafe == it.textSafe
                } == true

    /**
     * Object hash code
     *
     * @return Int
     */
    override fun hashCode(): Int = file.absolutePath.hashCode() + textSafe.hashCode() * 23

    @Suppress("TooGenericExceptionCaught")
    private val ExternalSourceCode.textSafe: String?
        get() =
            try {
                text
            } catch (e: Throwable) {
                null
            }
}
