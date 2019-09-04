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

package net.pototskiy.apps.lomout.loader

import kotlinx.coroutines.ObsoleteCoroutinesApi
import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.createContext
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.script.LomoutScript
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.WriterAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.filter.LevelRangeFilter
import org.apache.logging.log4j.core.layout.PatternLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import java.io.ByteArrayOutputStream
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Load data in production way")
@Execution(ExecutionMode.SAME_THREAD)
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
internal class DataLoadingInProductionWayTest {

    private lateinit var lomoutScript: LomoutScript
    private lateinit var repository: EntityRepositoryInterface
    private val logOut = ByteArrayOutputStream()
    private lateinit var appender: WriterAppender

    @BeforeAll
    internal fun initAll() {
        System.setSecurityManager(NoExitSecurityManager())
        val logger = LogManager.getLogger(ROOT_LOG_NAME) as Logger
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
        val layout = PatternLayout.newBuilder()
            .withPattern("%level,")
            .build()
        appender = WriterAppender.createAppender(
            layout,
            LevelRangeFilter.createFilter(Level.OFF, Level.ERROR, Filter.Result.NEUTRAL, Filter.Result.DENY),
            logOut.writer(),
            "test-catcher",
            false,
            true
        )
        appender.start()
        logger.addAppender(appender)
        val util = LoadingDataTestPrepare()
        println("config file: ${System.getenv("PRODUCTION_CONFIG")}")
        lomoutScript = util.loadConfiguration(System.getenv("PRODUCTION_CONFIG"))
        repository = EntityRepository(lomoutScript.database, Level.ERROR)
        LomoutContext.setContext(createContext {
            script = lomoutScript
            scriptFile = File(System.getenv("PRODUCTION_CONFIG"))
            this.logger = LogManager.getLogger(LOADER_LOG_NAME)
            this.repository = this@DataLoadingInProductionWayTest.repository
        })
    }

    @AfterAll
    internal fun tearDownAll() {
        repository.close()
    }

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @ObsoleteCoroutinesApi
    @Test
    @DisplayName("Load data according production config")
    internal fun loadDataTest() {
        DataLoader().load()
        assertThat(logOut.toString()).isEmpty()
    }

    @AfterAll
    internal fun finishAll() {
        val logger = LogManager.getLogger(ROOT_LOG_NAME) as Logger
        logger.removeAppender(appender)
        appender.stop()
    }
}
