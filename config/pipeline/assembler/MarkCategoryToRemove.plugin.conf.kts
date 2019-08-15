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

import MageCategory_conf.MageCategory
import net.pototskiy.apps.lomout.api.document.DocumentData
import kotlin.reflect.KClass

class MarkCategoryToRemove : PipelineAssemblerPlugin() {
    override fun assemble(target: KClass<out Document>, entities: EntityCollection): DocumentData {
        try {
            val category = entities[MageCategory::class] as MageCategory
            return documentData(
                MageCategory.attributes.getValue("entity_id") to category.entity_id,
                MageCategory.attributes.getValue("remove_flag") to true
            )
        } catch (e: Exception) {
            throw AppConfigException(suspectedLocation(target), e.message, e)
        }
    }
}
