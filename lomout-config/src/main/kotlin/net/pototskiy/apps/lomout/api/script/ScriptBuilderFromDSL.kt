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

import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import org.apache.logging.log4j.LogManager
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import kotlin.script.experimental.api.asSuccess
import kotlin.script.experimental.api.onFailure
import kotlin.script.experimental.api.onSuccess
import kotlin.system.exitProcess

/**
 * Configuration builder from DSL file
 *
 * @property scriptFile The configuration DSL file
 * @property cacheDir The directory to cache compiled configuration
 * @property doNotUseCache True — no not cache script, false — cache script
 * @property statusLog The status logger
 * @property scriptCache The backing config field
 * @property logger The configuration logger
 * @property lomoutScript The built config
 * @constructor
 */
class ScriptBuilderFromDSL(
    private val scriptFile: File,
    private val cacheDir: String = "tmp/config/cache",
    private val doNotUseCache: Boolean = false
) {
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)!!

    private var scriptCache: LomoutScript? = null
    private val logger = LogManager.getLogger(STATUS_LOG_NAME)
    val lomoutScript: LomoutScript
        get() {
            return scriptCache ?: readConfig().also { scriptCache = it }
        }

    private fun readConfig(): LomoutScript {
        val startTime = LocalDateTime.now()
        lateinit var lomoutScript: LomoutScript
        statusLog.info(message("message.info.script.load.start", scriptFile.absolutePath))
        val ivyFile = getIvyFile(scriptFile)
        val configHost = LomoutScriptHost(scriptFile, cacheDir, doNotUseCache, ivyFile)
        configHost.compile().onFailure {
            logger.error("Configuration file cannot be compiled")
        }.onSuccess { compileResult ->
            configHost.evaluate().onFailure {
                logger.error("Script cannot be evaluated")
                exitProcess(1)
            }.onSuccess { evaluationResult ->
                lomoutScript = evaluationResult
                evaluationResult.asSuccess()
            }
            compileResult.asSuccess()
        }
        val duration = Duration.between(startTime, LocalDateTime.now()).seconds
        statusLog.info(message("message.info.script.load.finish", duration))
        return lomoutScript
    }

    private fun getIvyFile(configFile: File): File? {
        val ivyFile = configFile.parentFile.resolve("ivy.xml")
        return if (ivyFile.exists()) {
            ivyFile
        } else {
            null
        }
    }
}
