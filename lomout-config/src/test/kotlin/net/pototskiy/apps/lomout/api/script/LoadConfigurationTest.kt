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
    private val compileErrorConf = "${System.getenv("TEST_DATA_DIR")}/compile-error.lomout.kts"
    private val evaluateErrorConf = "${System.getenv("TEST_DATA_DIR")}/evaluate-error.lomout.kts"
    private val errorFreeConf = "${System.getenv("TEST_DATA_DIR")}/error-free.lomout.kts"
    private val emptyConf = "${System.getenv("TEST_DATA_DIR")}/empty-config.lomout.kts"
    private val importErrorConf = "${System.getenv("TEST_DATA_DIR")}/import-error.lomout.kts"
    @Suppress("SpellCheckingInspection", "GraziInspection")
    private val depenedsOnErrorConf = "${System.getenv("TEST_DATA_DIR")}/dependson-error.lomout.kts"

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
            ScriptBuilderFromDSL(File(compileErrorConf)).lomoutScript
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(log).containsPattern("""Expecting '"' \(compile-error\.lomout\.kts:\d+\)""")
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadEmptyConfigTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ScriptBuilderFromDSL(File(emptyConf)).lomoutScript
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        @Suppress("RegExpRedundantEscape", "GraziInspection")
        assertThat(log).containsPattern("""\[ERROR\] .* - Script cannot be evaluated""")
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigWithEvaluateErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ScriptBuilderFromDSL(File(evaluateErrorConf)).lomoutScript
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(log).containsPattern("""Source file 'file-2' is not defined. \(evaluate-error\.lomout\.kts:\d+\)""")
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigImportErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ScriptBuilderFromDSL(File(importErrorConf)).lomoutScript
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(
            log.contains(
                Regex(
                    ".*ERROR.*- Source file or directory not found.*absent-file.plugin.lomout.kts"
                )
            )
        ).isEqualTo(true)
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigDependsOnErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ScriptBuilderFromDSL(File(depenedsOnErrorConf)).lomoutScript
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(
            log.contains(
                Regex(
                    ".*ERROR.*- Unable to resolve dependency.*absent-file.plugin.lomout.kts"
                )
            )
        ).isEqualTo(true)
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadErrorFreeConfigurationTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            val v = ScriptBuilderFromDSL(File(errorFreeConf))
            assertThat(v.lomoutScript).isNotNull
            assertThat(v.lomoutScript).isNotNull
        } catch (e: Exception) {
            assertThat(true).isEqualTo(true)
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(log.contains("ERROR")).isEqualTo(false)
    }
}
