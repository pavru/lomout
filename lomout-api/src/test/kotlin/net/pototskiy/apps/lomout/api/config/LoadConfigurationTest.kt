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

import net.pototskiy.apps.lomout.api.BuildInfo
import net.pototskiy.apps.lomout.api.LogCatcher
import net.pototskiy.apps.lomout.api.NoExitSecurityManager
import org.apache.logging.log4j.Level
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

@DisplayName("Just load configuration file")
internal class LoadConfigurationTest {
    private val logCatcher = LogCatcher()
    private val compileErrorConf = "${System.getenv("TEST_DATA_DIR")}/compile-error.conf.kts"
    private val evaluateErrorConf = "${System.getenv("TEST_DATA_DIR")}/evaluate-error.conf.kts"
    private val errorFreeConf = "${System.getenv("TEST_DATA_DIR")}/error-free.conf.kts"
    private val emptyConf = "${System.getenv("TEST_DATA_DIR")}/empty-config.conf.kts"
    private val importErrorConf = "${System.getenv("TEST_DATA_DIR")}/import-error.conf.kts"
    @Suppress("SpellCheckingInspection", "GraziInspection")
    private val depenedsOnErrorConf = "${System.getenv("TEST_DATA_DIR")}/dependson-error.conf.kts"

    @BeforeEach
    internal fun setUp() {
        System.setSecurityManager(NoExitSecurityManager())
        BuildInfo.dependencies.add(
            BuildInfo.Dependency(
                "implementation",
                "org.cache2k",
                "org.cache2k",
                "1.2.2.Final",
                emptyList()
            ))
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigWithCompileErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ConfigurationBuilderFromDSL(File(compileErrorConf)).config
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(log).contains("Expecting '\"' (compile-error.conf.kts:3)")
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadEmptyConfigTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ConfigurationBuilderFromDSL(File(emptyConf)).config
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        @Suppress("RegExpRedundantEscape", "GraziInspection")
        assertThat(log.contains(Regex("^\\[ERROR\\] .* - Script cannot be evaluated$"))).isEqualTo(true)
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigWithEvaluateErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ConfigurationBuilderFromDSL(File(evaluateErrorConf)).config
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(log).contains("Source file 'file-2' is not defined. (evaluate-error.conf.kts:20)")
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigImportErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ConfigurationBuilderFromDSL(File(importErrorConf)).config
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(
            log.contains(
                Regex(
                    ".*ERROR.*- Source file or directory not found.*absent-file.plugin.conf.kts"
                )
            )
        ).isEqualTo(true)
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigDependsOnErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ConfigurationBuilderFromDSL(File(depenedsOnErrorConf)).config
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(
            log.contains(
                Regex(
                    ".*ERROR.*- Unable to resolve dependency.*absent-file.plugin.conf.kts"
                )
            )
        ).isEqualTo(true)
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadErrorFreeConfigurationTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            val v = ConfigurationBuilderFromDSL(File(errorFreeConf))
            assertThat(v.config).isNotNull
            assertThat(v.config).isNotNull
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(log.contains("ERROR")).isEqualTo(false)
    }
}
