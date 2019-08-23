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

package net.pototskiy.apps.lomout.api.script.mediator

import net.pototskiy.apps.lomout.api.Generated
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import net.pototskiy.apps.lomout.api.script.LomoutDsl
import net.pototskiy.apps.lomout.api.document.Document
import kotlin.reflect.KClass

/**
 * Pipeline input entity
 *
 * @property entity EntityType The entity type
 * @property includeDeleted The flag to include deleted entities
 * @constructor
 */
data class InputEntity(
    val entity: KClass<out Document>,
    val includeDeleted: Boolean
) {

    /**
     * Pipeline input entity definition builder class
     *
     * @property helper The config build helper
     * @property entityType The base entity type
     * @property includeDeleted The flag to include deleted entity to pipeline
     * @constructor
     */
    @LomoutDsl
    class Builder(
        val helper: ScriptBuildHelper,
        val entityType: KClass<out Document>
    ) {
        private var includeDeleted: Boolean = false

        /**
         * Indicate that deleted entities must be included in a pipeline.
         */
        @LomoutDsl
        fun includeDeleted() {
            this.includeDeleted = true
        }

        /**
         * Build input entity definition
         *
         * @return InputEntity
         */
        fun build(): InputEntity = InputEntity(entityType, includeDeleted)
    }

    /**
     * Test equal
     *
     * @param other The other
     * @return The result
     */
    @Generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InputEntity) return false

        if (entity != other.entity) return false
        if (includeDeleted != other.includeDeleted) return false

        return true
    }

    /**
     * Generate has code
     *
     * @return Int
     */
    override fun hashCode(): Int {
        var result = entity.hashCode()
        result = 31 * result + includeDeleted.hashCode()
        return result
    }
}
