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

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.WriterAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.filter.LevelRangeFilter
import org.apache.logging.log4j.core.layout.PatternLayout
import java.io.ByteArrayOutputStream

class LogCatcher {
    private lateinit var logOut: ByteArrayOutputStream
    val log: String
        get() = logOut.toString()
    private lateinit var appender: WriterAppender
    private val logger = LogManager.getLogger(ROOT_LOG_NAME) as Logger
    private var layout = PatternLayout.newBuilder()
        .withPattern((logger.appenders["console"]?.layout as? PatternLayout)?.conversionPattern ?: "%level,")
        .build()

    fun startToCatch(minLevel: Level, maxLevel: Level) {
        logOut = ByteArrayOutputStream()
        appender = WriterAppender.createAppender(
            layout,
            LevelRangeFilter.createFilter(minLevel, maxLevel, Filter.Result.NEUTRAL, Filter.Result.DENY),
            logOut.writer(),
            "test-catcher",
            false,
            true
        )
        appender.start()
        logger.addAppender(appender)
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
    }

    fun stopToCatch() {
        logger.removeAppender(appender)
        appender.stop()
        logOut.close()
    }
}
