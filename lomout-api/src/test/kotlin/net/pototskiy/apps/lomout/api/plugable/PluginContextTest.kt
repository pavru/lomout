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

package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.MEDIATOR_LOG_NAME
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import org.apache.logging.log4j.LogManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PluginContextTest {
    @Test
    internal fun contextLogTest() {
        val plugin = TestPlugin()
        assertThat(plugin.testLog(ROOT_LOG_NAME)).isEqualTo(true)
        PluginContext.logger = LogManager.getLogger(LOADER_LOG_NAME)
        assertThat(plugin.testLog(LOADER_LOG_NAME)).isEqualTo(true)
        assertThat(plugin.testLog(MEDIATOR_LOG_NAME)).isEqualTo(false)
        PluginContext.logger = LogManager.getLogger(MEDIATOR_LOG_NAME)
        assertThat(plugin.testLog(MEDIATOR_LOG_NAME)).isEqualTo(true)
        assertThat(plugin.testLog(LOADER_LOG_NAME)).isEqualTo(false)
    }

    class TestPlugin : Plugin() {
        fun testLog(name: String): Boolean {
            return logger.name == name
        }
    }
}
