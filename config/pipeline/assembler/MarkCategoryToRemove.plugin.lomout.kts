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
@file:Import("../../entity/ImportCategory.lomout.kts")

import ImportCategory_lomout.ImportCategory
import MageCategory_lomout.MageCategory
import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.callable.PipelineAssembler
import net.pototskiy.apps.lomout.api.document.DocumentData
import net.pototskiy.apps.lomout.api.entity.EntityCollection
import net.pototskiy.apps.lomout.api.suspectedLocation
import org.jetbrains.kotlin.script.util.Import
import kotlin.reflect.KClass

class MarkCategoryToRemove : PipelineAssembler<ImportCategory>() {
    override fun assemble(entities: EntityCollection): ImportCategory {
        val doc = ImportCategory()
        try {
            val category = entities[MageCategory::class] as MageCategory
            doc.entity_id = category.entity_id
            doc.remove_flag = true
            return doc
        } catch (e: Exception) {
            throw AppConfigException(suspectedLocation(ImportCategory::class), e.message, e)
        }
    }
}
