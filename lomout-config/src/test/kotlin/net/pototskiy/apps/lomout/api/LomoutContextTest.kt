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

package net.pototskiy.apps.lomout.api

import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.script.LomoutScript
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class LomoutContextTest {
    private val lomoutScript = LomoutScript.Builder(ScriptBuildHelper()).apply {
        database {
            name("lomout_test")
        }
    }.build()
    private lateinit var repo: EntityRepositoryInterface

    @BeforeEach
    internal fun setUp() {
        repo = EntityRepository(lomoutScript.database, Level.WARN)
    }

    @AfterEach
    internal fun tearDown() {
        repo.close()
    }

    @Test
    internal fun successfulCreateContextTest() {
        val context = createContext {
            script = lomoutScript
            logger = LogManager.getLogger(LOADER_LOG_NAME)
            scriptFile = File("mock.lomout.kts")
            repository = repo
            parameters = mapOf("param1" to "value1")
        }
        assertThat(context.script).isSameAs(lomoutScript)
        assertThat(context.logger.name).isEqualTo(LOADER_LOG_NAME)
        assertThat(context.scriptFile.name).isEqualTo("mock.lomout.kts")
        assertThat(context.repository).isSameAs(repo)
        assertThat(context.getParameter("param1")).isEqualTo("value1")
        val context1 = createContext(context) {
            parameters = mapOf("param1" to "value2")
        }
        assertThat(context1.script).isSameAs(lomoutScript)
        assertThat(context1.logger.name).isEqualTo(LOADER_LOG_NAME)
        assertThat(context1.scriptFile.name).isEqualTo("mock.lomout.kts")
        assertThat(context1.repository).isSameAs(repo)
        assertThat(context1.getParameter("param1")).isEqualTo("value2")
    }

    @Test
    internal fun unsuccessfulCreateContextTest() {
        assertThatThrownBy {
            createContext {
            }
        }.isInstanceOf(AppException::class.java)
            .hasMessage("Context script cannot be null.")
        assertThatThrownBy {
            createContext {
                script = lomoutScript
            }
        }.isInstanceOf(AppException::class.java)
            .hasMessage("Script file cannot be null in context.")
        assertThatThrownBy {
            createContext {
                script = lomoutScript
                scriptFile = File("mock.lomout.kts")
            }
        }.isInstanceOf(AppException::class.java)
            .hasMessage("Context repository cannot be null.")
    }
}
