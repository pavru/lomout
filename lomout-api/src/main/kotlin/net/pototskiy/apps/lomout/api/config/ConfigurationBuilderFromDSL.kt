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
 * @property configFile The configuration DSL file
 * @property cacheDir The directory to cache compiled configuration
 * @property doNotUseCache True — no not cache script, false — cache script
 * @property statusLog The status logger
 * @property configCache The backing config field
 * @property logger The configuration logger
 * @property config The built config
 * @constructor
 */
class ConfigurationBuilderFromDSL(
    private val configFile: File,
    private val cacheDir: String = "tmp/config/cache",
    private val doNotUseCache: Boolean = false
) {
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)!!

    private var configCache: Config? = null
    private val logger = LogManager.getLogger(STATUS_LOG_NAME)
    val config: Config
        get() {
            return configCache ?: readConfig().also { configCache = it }
        }

    private fun readConfig(): Config {
        val startTime = LocalDateTime.now()
        lateinit var evaluatedConfig: Config
        statusLog.info(message("message.info.config.load.start", configFile.absolutePath))
        val ivyFile = getIvyFile(configFile)
        val configHost = ConfigHost(configFile, cacheDir, doNotUseCache, ivyFile)
        configHost.compile().onFailure {
            logger.error("Configuration file cannot be compiled")
        }.onSuccess { compileResult ->
            configHost.evaluate().onFailure {
                logger.error("Script cannot be evaluated")
                exitProcess(1)
            }.onSuccess { evaluationResult ->
                evaluatedConfig = evaluationResult
                evaluationResult.asSuccess()
            }
            compileResult.asSuccess()
        }
        val duration = Duration.between(startTime, LocalDateTime.now()).seconds
        statusLog.info(message("message.info.config.load.finish", duration))
        return evaluatedConfig
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
