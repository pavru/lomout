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

import MageCategory_lomout.MageCategory
import OnecGroup_lomout.OnecGroup
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.callable.PipelineClassifier
import net.pototskiy.apps.lomout.api.script.pipeline.ClassifierElement
import net.pototskiy.apps.lomout.api.suspectedLocation

class CategoryClassifier : PipelineClassifier() {
    override fun classify(element: ClassifierElement): ClassifierElement {
        try {
            val entities = element.entities
            val group = entities[OnecGroup::class] as OnecGroup
            val category = entities[MageCategory::class] as MageCategory
            val categoryPath = category.__path
            if (categoryPath in rootMageCategories) return element.mismatch()
            if (group.transformed_path == categoryPath) return element.match()
            return element.mismatch()
        } catch (e: Exception) {
            throw AppDataException(suspectedLocation(), e.message, e)
        }
    }

    companion object {
        private val rootMageCategories = listOf(
            "/Root Catalog",
            "/Root Catalog/Default Category",
            "/Root Catalog/Default Category/Каталог"
        )
    }
}
