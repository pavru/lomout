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

import net.pototskiy.apps.lomout.api.AppException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

/**
 * Plugin context
 */
object PluginContext : PluginContextInterface {
    /**
     * Context configuration
     */
    override lateinit var config: Config
    /**
     * Context logger
     */
    override var logger: Logger = LogManager.getLogger(ROOT_LOG_NAME)

    private var bScriptFile: File? = null
    /**
     * Main script file
     */
    override var scriptFile: File
        get() = bScriptFile ?: throw AppException(message = message("message.error.plugin.bad_script"))
        set(value) {
            bScriptFile = value
        }
    /**
     * Entity repository
     */
    override lateinit var repository: EntityRepositoryInterface
}
