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

import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class MainAndIdeLoggerTest {

    @org.junit.jupiter.api.Test
    fun info() {
        val logger = MainAndIdeLogger()
        logger.info("Test message", Exception("Test exception"))
        assert(true)
    }

    @org.junit.jupiter.api.Test
    fun warn() {
        val logger = MainAndIdeLogger()
        logger.warn("Test message", Exception("Test exception"))
        assert(true)
    }

    @org.junit.jupiter.api.Test
    fun error() {
        val logger = MainAndIdeLogger()
        logger.error("Test message", Exception("Test exception"))
        assert(true)
    }

    @org.junit.jupiter.api.Test
    fun trace() {
        val logger = MainAndIdeLogger()
        logger.trace("Test message", Exception("Test exception"))
        assert(true)
    }

    @org.junit.jupiter.api.Test
    fun debug() {
        val logger = MainAndIdeLogger()
        logger.debug("Test message", Exception("Test exception"))
        assert(true)
    }
}
