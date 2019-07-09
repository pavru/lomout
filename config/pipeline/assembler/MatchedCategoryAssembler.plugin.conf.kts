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
import OnecGroup_conf.OnecGroup
import kotlin.collections.set
import kotlin.reflect.KClass

class MatchedCategoryAssembler : PipelineAssemblerPlugin() {
    override fun assemble(target: KClass<out Document>, entities: EntityCollection): Map<Attribute, Any> {
        val data = mutableMapOf<Attribute, Any>()
        try {
            val mageCategory = entities[MageCategory::class] as MageCategory
            val onecGroup = entities[OnecGroup::class]
            target.documentMetadata.attributes.values.forEach { targetAttr ->
                if (mageCategory.getAttribute(targetAttr.name) != null) {
                    data[targetAttr] = mageCategory.getAttribute(targetAttr.name)!!
                } else if (onecGroup.getAttribute(targetAttr) != null) {
                    data[targetAttr] = onecGroup.getAttribute(targetAttr.name)!!
                }
            }
            data[target.documentMetadata.attributes.getValue("remove_flag")] = false
        } catch (e: Exception) {
            throw AppDataException(badPlace(target), e.message, e)
        }
        return data
    }
}
