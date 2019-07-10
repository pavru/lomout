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

package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import org.bson.conversions.Bson
import org.litote.kmongo.EMPTY_BSON
import org.litote.kmongo.and
import org.litote.kmongo.eq

/**
 * Create Bson filter from the map of attribute. Logical and is used for concatenation.
 *
 * @receiver Map<Attribute, Any>
 * @return Bson
 */
@Suppress("SpreadOperator")
fun Map<Attribute, Any>.toFilter(): Bson {
    val bson = this.map { it.key.property eq it.value }.toTypedArray()
    return when {
        bson.isEmpty() -> EMPTY_BSON
        bson.size == 1 -> bson[0]
        else -> and(*bson)
    }
}
