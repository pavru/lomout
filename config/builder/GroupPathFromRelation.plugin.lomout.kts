
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

import OnecGroupRelation_lomout.OnecGroupRelation
import OnecGroup_lomout.OnecGroup
import org.apache.commons.collections4.map.LRUMap
import org.bson.types.ObjectId
import org.litote.kmongo.eq
import java.lang.ref.WeakReference
import java.util.Collections.*
import kotlin.collections.set

class GroupPathFromRelation(
    private val separator: String = "/",
    private val root: String = ""
) : AttributeBuilder<String?>() {

    override fun build(entity: Document): String? {
        logger.info("test logger")
        entity as OnecGroup
        val pathFromCache = pathCache[entity._id]?.get()
        if (pathFromCache != null) return pathFromCache
        val path = mutableListOf<String>()
        var relationEntity = repository.get(
            OnecGroupRelation::class,
            OnecGroupRelation::group_code eq entity.group_code
        ) as? OnecGroupRelation ?: return null
        var name: String? = relationEntity.group_name
        while (name != null) {
            path.add(name)
            val parent = relationEntity.group_parent_code ?: break
            relationEntity = repository.get(
                OnecGroupRelation::class,
                OnecGroupRelation::group_code eq parent
            ) as? OnecGroupRelation ?: break
            name = relationEntity.group_name
        }
        return "$root${path.reversed().joinToString(separator)}".also {
            pathCache[entity._id] = WeakReference(it)
        }
    }

    companion object {
        private val pathCache = synchronizedMap(LRUMap<ObjectId, WeakReference<String>>(200, 100))
    }
}
