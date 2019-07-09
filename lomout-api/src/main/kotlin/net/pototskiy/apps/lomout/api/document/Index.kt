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

package net.pototskiy.apps.lomout.api.document

/**
 * Attribute indexes.
 *
 * @property indexes Indexes array
 * @constructor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Indexes(
    val indexes: Array<Index>
)

/**
 * Attribute index.
 *
 * @property name The index name
 * @property sortOrder The sort order
 * @property isUnique
 * @constructor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Index(
    val name: String,
    val sortOrder: SortOrder = SortOrder.ASC,
    val isUnique: Boolean = false
) {
    /**
     * Sort order
     */
    enum class SortOrder {
        /**
         * Ascending sort
         *
         */
        ASC,
        /**
         * Descending sort
         *
         */
        DESC
    }
}
