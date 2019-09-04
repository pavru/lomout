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
import OnecGroup_lomout.OnecGroup
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.callable.PipelineAssembler
import net.pototskiy.apps.lomout.api.entity.EntityCollection
import org.jetbrains.kotlin.script.util.Import

class CategoryFromGroupAssembler : PipelineAssembler<ImportCategory>() {
    override operator fun invoke(entities: EntityCollection, context: LomoutContext): ImportCategory? {
        val data = ImportCategory()
        entities.getOrNull(OnecGroup::class)?.let { onec ->
            data.documentMetadata.attributes.values.forEach { attr ->
                onec.getAttribute(attr.name)?.let { data.setAttribute(attr, it) }
            }
        }
        return data
    }
}
