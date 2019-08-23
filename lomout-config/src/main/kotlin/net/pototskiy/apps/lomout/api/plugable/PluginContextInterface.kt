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

import net.pototskiy.apps.lomout.api.script.LomoutScript
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import org.apache.logging.log4j.Logger
import java.io.File

/**
 * Plugin context
 *
 * @property lomoutScript Config
 * @property logger Logger
 */
interface PluginContextInterface {
    /**
     * Context configuration
     */
    var lomoutScript: LomoutScript
    /**
     * Context logger
     */
    var logger: Logger
    /**
     * Main script file
     */
    var scriptFile: File
    /**
     * Entity repository
     */
    var repository: EntityRepositoryInterface
}
