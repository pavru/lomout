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

package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.unknownPlace
import kotlin.reflect.KClass

/**
 * Entity collection, contains only one entity per type
 *
 * @property data Entities
 * @constructor
 */
class EntityCollection(private val data: List<Document>) : List<Document> by data {
    private val entityMap = data.map { it::class to it }.toMap()
    /**
     * Get entity by type name
     *
     * @param type The entity type name
     * @return Entity
     * @throws AppDataException Entity not found
     */
    operator fun get(type: KClass<out Document>): Document {
        return entityMap[type]
            ?: throw AppDataException(
                unknownPlace(),
                message("message.error.pipeline.no_entity_in_collection", type.qualifiedName)
            )
    }

    /**
     * Get entity by type name
     *
     * @param type The entity type name
     * @return The entity or null
     */
    fun getOrNull(type: KClass<out Document>): Document? = entityMap[type]
}
