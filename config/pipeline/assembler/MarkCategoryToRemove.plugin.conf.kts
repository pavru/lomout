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
@file:Import("../../entity/ImportCategory.conf.kts")

import ImportCategory_conf.ImportCategory
import MageCategory_conf.MageCategory
import net.pototskiy.apps.lomout.api.document.DocumentData
import org.jetbrains.kotlin.script.util.Import
import kotlin.reflect.KClass

class MarkCategoryToRemove : PipelineAssemblerPlugin() {
    override fun assemble(entities: EntityCollection): Document {
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
