
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

import OnecGroupExtended_lomout.OnecGroupExtended
import OnecGroup_lomout.OnecGroup
import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.callable.AttributeBuilder
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.suspectedLocation
import org.litote.kmongo.eq

open class GroupToCategoryPath : AttributeBuilder<String?>() {
    override fun build(entity: Document): String? {
        try {
            entity as OnecGroup
            val extendedInfo = OnecGroupExtended::class
            val groupId = entity.group_code
            val entityExtInfo = repository.get(
                extendedInfo,
                OnecGroupExtended::group_code eq groupId
            ) as? OnecGroupExtended
            return entityExtInfo?.magento_path
        } catch (e: Exception) {
            throw AppConfigException(suspectedLocation(entity::class), e.message, e)
        }
    }
}
