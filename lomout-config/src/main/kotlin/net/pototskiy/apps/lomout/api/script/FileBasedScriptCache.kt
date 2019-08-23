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

package net.pototskiy.apps.lomout.api.script

import net.pototskiy.apps.lomout.api.Generated
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.jvm.CompiledJvmScriptsCache
import kotlin.script.experimental.jvm.impl.KJvmCompiledScript

/**
 * File base compiled script cache
 *
 * @property baseDir The cache directory
 * @property doNotUseCache True — do not use cache, false — cache script
 * @constructor
 */
@Suppress("unused")
@Generated
class FileBasedScriptCache(
    private val baseDir: File,
    private val doNotUseCache: Boolean
) : CompiledJvmScriptsCache {
    private val logger = LogManager.getLogger(STATUS_LOG_NAME)
    /**
     * Try get script from the cache
     *
     * @param script The script source code
     * @param scriptCompilationConfiguration ScriptCompilationConfiguration
     * @return CompiledScript<*>?
     */
    @Suppress("ReturnCount")
    override fun get(
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ): CompiledScript<*>? {
        if (doNotUseCache) return null
        val scriptHash = ScriptUniqueHash(script, scriptCompilationConfiguration).hash()
            ?: return null
        val file = File(baseDir, scriptHash)
        return if (!file.exists()) null else file.readCompiledScript()
    }

    /**
     * Store compiled script in the cache
     *
     * @param compiledScript The compiled script
     * @param script SourceCode The script source code
     * @param scriptCompilationConfiguration ScriptCompilationConfiguration
     */
    override fun store(
        compiledScript: CompiledScript<*>,
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ) {
        if (doNotUseCache) return
        if (!baseDir.exists()) baseDir.mkdirs()
        val scriptHash = ScriptUniqueHash(script, scriptCompilationConfiguration).hash()
        if (scriptHash == null) {
            logger.warn(message("message.error.script.cache.wrong.hash"))
            return
        }
        val file = File(baseDir, scriptHash)
        file.outputStream().use { fs ->
            ObjectOutputStream(fs).use { os ->
                os.writeObject(compiledScript)
            }
        }
    }

    private fun File.readCompiledScript(): CompiledScript<*> {
        return inputStream().use { fs ->
            ObjectInputStream(fs).use { os ->
                (os.readObject() as KJvmCompiledScript<*>)
            }
        }
    }
}
