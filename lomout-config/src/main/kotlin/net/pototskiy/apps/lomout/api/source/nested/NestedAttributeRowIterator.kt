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

package net.pototskiy.apps.lomout.api.source.nested

/**
 * Attribute workbook row iterator
 *
 * @property sheet NestedAttributeSheet
 * @property index Int
 * @constructor
 */
class NestedAttributeRowIterator(private val sheet: NestedAttributeSheet) : Iterator<NestedAttributeRow> {
    private var index = 0
    /**
     * Test if sheet has a next row
     * @return Boolean
     */
    override fun hasNext(): Boolean = index < 1

    /**
     * Get next sheet row
     *
     * @return NestedAttributeRow
     */
    override fun next(): NestedAttributeRow = sheet[index++] as NestedAttributeRow
}
