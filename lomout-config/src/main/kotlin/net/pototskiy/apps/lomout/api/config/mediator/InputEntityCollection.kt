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

package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.unknownPlace
import kotlin.reflect.KClass

/**
 * Pipeline input entities configuration
 *
 * @property entities List<InputEntity>
 * @constructor
 */
data class InputEntityCollection(private val entities: List<InputEntity>) : List<InputEntity> by entities {
    /**
     * Input entities builder class
     *
     * @property helper ConfigBuildHelper The config build helper
     * @property entities MutableList<InputEntity> Input entities
     * @constructor
     */
    @ConfigDsl
    class Builder(val helper: ConfigBuildHelper) {
        private val entities = mutableListOf<InputEntity>()

        /**
         * Define input entity as reference to already defined one
         *
         * ```
         * ...
         *  entity(entityClass) {
         *      includeDeleted()
         *  }
         * ...
         * ```
         * * entityClass — The entity type class
         * * includeDeleted() — Set the flag to include deleted entities
         *
         * @param entityType The entity type class
         * @param block The entity definition
         */
        @ConfigDsl
        fun entity(entityType: KClass<out Document>, block: InputEntity.Builder.() -> Unit = {}) {
            entities.add(
                InputEntity
                    .Builder(helper, entityType)
                    .apply(block)
                    .build()
            )
        }

        /**
         * Build input entities collection
         *
         * @return InputEntityCollection
         */
        fun build(): InputEntityCollection {
            if (entities.isEmpty()) {
                throw AppConfigException(unknownPlace(), message("message.error.config.pipeline.input.one_must_be"))
            }
            return InputEntityCollection(entities)
        }
    }
}
