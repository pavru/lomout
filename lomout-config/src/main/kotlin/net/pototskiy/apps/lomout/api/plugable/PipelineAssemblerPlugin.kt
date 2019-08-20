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

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.entity.EntityCollection

/**
 * Base class for any pipeline assemblers
 */
abstract class PipelineAssemblerPlugin : Plugin() {
    /**
     * Assembler function
     *
     * @param target The target entity type
     * @param entities The pipeline entity collection
     * @return The attribute→value map for target entity
     */
    abstract fun assemble(entities: EntityCollection): Document
}

/**
 * Function type for inline pipeline assembler
 */
typealias PipelineAssemblerFunction =
        PluginContextInterface.(entities: EntityCollection) -> Document
