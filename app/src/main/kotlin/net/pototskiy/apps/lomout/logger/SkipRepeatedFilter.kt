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

package net.pototskiy.apps.lomout.logger

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.Core
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Filter.Result
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import org.apache.logging.log4j.core.filter.AbstractFilter

@Suppress("unused")
@Plugin(
    name = "SkipRepeatedFilter",
    category = Core.CATEGORY_NAME,
    elementType = Filter.ELEMENT_TYPE,
    printObject = true
)
class SkipRepeatedFilter(
    onMatch: Result,
    onMismatch: Result
) : AbstractFilter(onMatch, onMismatch) {

    override fun filter(event: LogEvent?): Result {
        return if (lastMsg == null) {
            updateLast(event)
            return onMatch
        } else {
            val matched = lastMsg == event?.message?.formattedMessage &&
                    lastThread == event?.threadName &&
                    lastLogger == event?.loggerName &&
                    lastLevel == event?.level
            updateLast(event)
            if (matched) {
                onMismatch
            } else {
                onMatch
            }
        }
    }

    private fun updateLast(event: LogEvent?) {
        lastMsg = event?.message?.formattedMessage
        lastThread = event?.threadName
        lastLogger = event?.loggerName
        lastLevel = event?.level
    }

    companion object {
        var lastMsg: String? = null
        var lastThread: String? = null
        var lastLogger: String? = null
        var lastLevel: Level? = null

        @JvmStatic
        @PluginFactory
        fun createSkipRepeatedFilter(
            @PluginAttribute(value = "onMatch", defaultString = "NEUTRAL") onMatch: Result,
            @PluginAttribute(value = "onMismatch", defaultString = "DENY") onMismatch: Result
        ): SkipRepeatedFilter {
            return SkipRepeatedFilter(onMatch, onMismatch)
        }
    }
}
