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
import net.pototskiy.apps.lomout.api.callable.AttributeBuilder
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.document.Document
import org.apache.commons.collections4.map.LRUMap
import org.bson.types.ObjectId
import java.lang.ref.WeakReference
import java.util.Collections.*

class CategoryPathFromRelation(
    private val separator: String = "/",
    private val root: String = ""
) : AttributeBuilder<String?>() {

    override operator fun invoke(entity: Document, context: LomoutContext): String? {
        entity as MageCategory
        val cachedPath = pathCache[entity._id]?.get()
        if (cachedPath != null) return cachedPath
        val path = mutableListOf<String>()
        var current: MageCategory? = entity
        var name: String? = entity.name
        while (name != null && current != null) {
            path.add(name)
            val parentId = current.parent_id
            current = current.let {
                context.repository.get(
                    MageCategory::class,
                    mapOf(MageCategory.attributes.getValue("parent_id") to parentId)
                ) as? MageCategory
            }
            name = current?.name
        }
        return "$root${path.reversed().joinToString(separator)}".also {
            pathCache[entity._id] = WeakReference(it)
        }
    }

    companion object {
        private val pathCache = synchronizedMap(LRUMap<ObjectId, WeakReference<String>>(200, 100))
        private const val eTypeName = "mage-category"
    }
}
