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

package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentData
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import kotlin.reflect.KClass

class EntityUpdater(
    private val repository: EntityRepositoryInterface,
    private val entityType: KClass<out Document>
) {

    fun update(data: DocumentData): Long {
        val processedRows: Long
        var entity = repository.get(
            entityType,
            data.filter { it.key.isKey },
            includeDeleted = true
        )
        if (entity == null) {
            entity = repository.create(entityType)
            data.forEach { entity.setAttribute(it.key.name, it.value) }
            repository.update(entity)
            processedRows = 1L
        } else {
            entity.touch()
            processedRows = testAndUpdateTypedAttributes(entity, data)
            if (processedRows != 0L) {
                entity.markUpdated()
                repository.update(entity)
            } else {
                repository.updateCommonPart(entity)
            }
        }
        return processedRows
    }

    private fun testAndUpdateTypedAttributes(entity: Document, data: Map<Attribute, Any>): Long {
        var updatedRows = 0L
        entity.documentMetadata.attributes.values.forEach {
            if (data.containsKey(it) && entity.getAttribute(it.name) != data[it]) {
                entity.setAttribute(it.name, data[it])
                updatedRows = 1L
            }
        }
        return updatedRows
    }
}
