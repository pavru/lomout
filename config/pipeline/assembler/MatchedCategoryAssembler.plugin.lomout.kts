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
import OnecGroup_lomout.OnecGroup
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.callable.PipelineAssembler
import net.pototskiy.apps.lomout.api.entity.EntityCollection
import net.pototskiy.apps.lomout.api.suspectedLocation
import org.jetbrains.kotlin.script.util.Import

class MatchedCategoryAssembler : PipelineAssembler<ImportCategory>() {
    override operator fun invoke(entities: EntityCollection, context: LomoutContext): ImportCategory? {
        val data = ImportCategory()
        try {
            val mageCategory = entities[MageCategory::class] as MageCategory
            val onecGroup = entities[OnecGroup::class]
            data.documentMetadata.attributes.values.forEach { targetAttr ->
                if (mageCategory.getAttribute(targetAttr.name) != null) {
                    data.setAttribute(targetAttr, mageCategory.getAttribute(targetAttr.name)!!)
                } else if (onecGroup.getAttribute(targetAttr) != null) {
                    data.setAttribute(targetAttr, onecGroup.getAttribute(targetAttr.name)!!)
                }
            }
            data.remove_flag = false
        } catch (e: Exception) {
            throw AppDataException(suspectedLocation(ImportCategory::class), e.message, e)
        }
        return data
    }
}
