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

import kotlinx.coroutines.asContextElement
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.script.LomoutScript
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Plugin context
 *
 * @property script Config
 * @property logger Logger
 */
class LomoutContext(
    /**
     * Context configuration
     */
    val script: LomoutScript,
    /**
     * Context logger
     */
    val logger: Logger,
    /**
     * Main script file
     */
    val scriptFile: File,
    /**
     * Entity repository
     */
    val repository: EntityRepositoryInterface,
    /**
     * Script parameters map
     */
    val parameters: Map<String, String>
) : AbstractCoroutineContextElement(LomoutContext) {

    /**
     * Get script parameter
     *
     * @param key The parameter name(key)
     */
    fun getParameter(key: String): String? = parameters[key]

    /**
     * Create coroutine context element
     */
    fun asCoroutineContext() = threadLomoutContext.asContextElement(this)

    companion object Key : CoroutineContext.Key<LomoutContext> {
        /**
         * Thread variable to store lomout context
         */
        private val threadLomoutContext = ThreadLocal<LomoutContext>()

        /**
         * Get the current thread lomout context
         */
        fun getContext(): LomoutContext {
            return threadLomoutContext.get() ?: throw AppException(suspectedLocation(), "There is no lomout context.")
        }

        /**
         * Set the current thread lomout context
         *
         * @param value New lomout context
         */
        fun setContext(value: LomoutContext) = threadLomoutContext.set(value)
    }

    class Builder {
        var script: LomoutScript? = null
        var logger: Logger? = null
        var scriptFile: File? = null
        var repository: EntityRepositoryInterface? = null
        var parameters: Map<String, String>? = null

        @Suppress("ThrowsCount")
        fun build(): LomoutContext {
            return LomoutContext(
                script ?: throw AppConfigException(
                    suspectedLocation(),
                    MessageBundle.message("message.error.context.null_script")
                ),
                logger ?: LogManager.getLogger(ROOT_LOG_NAME),
                scriptFile ?: throw AppConfigException(
                    suspectedLocation(),
                    MessageBundle.message("message.error.context.bad_file")
                ),
                repository ?: throw AppConfigException(
                    suspectedLocation(),
                    MessageBundle.message("message.error.context.bad_repository")
                ),
                parameters ?: emptyMap()
            )
        }
    }
}

/**
 * Create lomout context
 *
 * @param block The [LomoutContext.Builder] apply block
 */
fun createContext(block: LomoutContext.Builder.() -> Unit): LomoutContext =
    LomoutContext.Builder().apply(block).build()

/**
 * Create lomout context base on another one.
 *
 * @param context The base context
 * @param block The [LomoutContext.Builder] apply block
 */
fun createContext(
    context: LomoutContext,
    block: LomoutContext.Builder.() -> Unit
): LomoutContext = LomoutContext.Builder().apply {
    script = context.script
    logger = context.logger
    scriptFile = context.scriptFile
    repository = context.repository
    parameters = context.parameters
}.apply(block).build()
